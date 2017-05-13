/**
 * Copyright (C) 2017
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.solid.generator;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;

import de.flapdoodle.solid.formatter.DefaultObjectFormatter;
import de.flapdoodle.solid.generator.PathRenderer.FormatterOfProperty;
import de.flapdoodle.solid.parser.content.Blob;
import de.flapdoodle.solid.parser.content.Site;
import de.flapdoodle.solid.parser.path.Path;
import de.flapdoodle.solid.site.PathProperties;
import de.flapdoodle.solid.site.Urls.Config;
import de.flapdoodle.solid.theme.Renderer;
import de.flapdoodle.solid.types.Collectors;
import de.flapdoodle.solid.types.Pair;

public class DefaultSiteGenerator implements SiteGenerator {

	private final PropertyResolver propertyResolver;
	private final PathRenderer pathRenderer;
	private final FilterFactory filterFactory;

	public DefaultSiteGenerator(PropertyResolver propertyResolver, PathRenderer pathRenderer, FilterFactory filterFactory) {
		this.propertyResolver = propertyResolver;
		this.pathRenderer = pathRenderer;
		this.filterFactory = filterFactory;
	}
	
	@Override
	public ImmutableList<Document> generate(Site site) {
		System.out.println(" -> "+site);
		
		System.out.println("dates -> "+metaValues(site, "date"));
		System.out.println("titles -> "+metaValues(site, "title"));
		
		ImmutableList.Builder<Document> documents=ImmutableList.builder();
		
		FormatterOfProperty propertyFormatter=formatterOfProperty(site);
		
		PathProperties pathProperties = site.config().pathProperties().merge(PathProperties.defaults());
		site.config().urls().configs().forEach((String name, Config config) -> {
			Path currentPath = config.path();

			ImmutableMultimap<ImmutableMap<String, Object>, Blob> groupedBlobs = filter(site.blobs(), filterFactory.filters(config.filters(), site.config().filters().filters())).stream()
				.collect(Collectors.groupingBy(blob -> pathPropertiesOf(blob, pathProperties::mapped, currentPath, propertyResolver)));

			if (currentPath.propertyNames().contains(Path.PAGE)) {
				groupedBlobs=groupByPage(groupedBlobs, Path.PAGE, config.itemsPerPage().orElse(10));
			}
			
			System.out.println(name);
			groupedBlobs.asMap().forEach((key, blobs) -> {
				Optional<String> renderedPath = pathRenderer.render(currentPath, key, propertyFormatter);
				System.out.println(" "+key+" -> "+blobs.size()+" --> "+renderedPath.orElse("---"));
				if (renderedPath.isPresent()) {
					Content renderedResult = site.theme().rendererFor(name).render(Renderer.Renderable.builder()
							.addAllBlobs(blobs)
							.context(Renderer.Context.builder()
									.putAllPathProperties(key)
									.site(site)
									.build())
							.build());
					
					documents.add(Document.builder()
						.path(renderedPath.get())
						.content(renderedResult)
						.build());
				}
			});
			
			
			
		});
		
		return documents.build();
	}

	private ImmutableList<Blob> filter(ImmutableList<Blob> src, Predicate<Blob> filter) {
		return src.stream().filter(filter).collect(ImmutableList.toImmutableList());
	}

	private FormatterOfProperty formatterOfProperty(Site site) {
		DefaultObjectFormatter defaultFormatter=new DefaultObjectFormatter();
		return (name,formatterName) -> {
			if (formatterName.isPresent()) {
				return Preconditions.checkNotNull(site.config().formatters().formatters().get(formatterName.get()),"could not get formatter %s",formatterName.get());
			}
			Optional<String> defaultFormatterName = Optional.ofNullable(site.config().defaultFormatter().get(name));
			if (defaultFormatterName.isPresent()) {
				return Preconditions.checkNotNull(site.config().formatters().formatters().get(defaultFormatterName.get()),"could not get formatter %s",defaultFormatterName.get());
			}
			return defaultFormatter;
		};
	}
	
	private static ImmutableMultimap<ImmutableMap<String, Object>, Blob> groupByPage(ImmutableMultimap<ImmutableMap<String, Object>, Blob> src, String pageKey, int itemsPerPage) {
		ImmutableMultimap.Builder<ImmutableMap<String, Object>, Blob> builder = ImmutableMultimap.<ImmutableMap<String, Object>, Blob>builder();
		
		src.asMap().forEach((key, blobs) -> {
			Iterable<List<Blob>> partitions = Iterables.partition(blobs, itemsPerPage);
			AtomicInteger currentPage=new AtomicInteger(0);
			partitions.forEach((List<Blob> partition) -> {
				ImmutableMap<String, Object> newKey = ImmutableMap.<String, Object>builder().putAll(key)
					.put(pageKey, currentPage.incrementAndGet())
					.build();
				builder.putAll(newKey, partition);
			});
		});
		
		return builder.build();
	}
	
	private static ImmutableMap<String, Object> pathPropertiesOf(Blob blob, Function<String, Collection<String>> pathPropertyMapping, Path path, PropertyResolver propertyResolver) {
		ImmutableList<String> pathProperties = path.propertyNames().stream()
			.filter(p -> !Path.PAGE.equals(p))
			.collect(ImmutableList.toImmutableList());
		
		ImmutableMap<String, Object> blopPathPropertyMap = pathProperties.stream()
			.map(p -> Pair.<String, Optional<?>>of(p, propertyOf(blob, pathPropertyMapping, propertyResolver, p)))
			.filter(pair -> pair.b().isPresent())
			.map(pair -> Pair.<String, Object>of(pair.a(), pair.b().get()))
			.collect(ImmutableMap.toImmutableMap(Pair::a, Pair::b));
		
		return blopPathPropertyMap;
	}

	private static Optional<?> propertyOf(Blob blob, Function<String, Collection<String>> pathPropertyMapping, PropertyResolver propertyResolver,
			String propertyName) {
		Collection<String> aliasList = pathPropertyMapping.apply(propertyName);
		for (String alias : aliasList) {
			Optional<?> resolved = propertyResolver.resolve(blob.meta(), Splitter.on('.').split(alias));
			if (resolved.isPresent()) {
				return resolved;
			}
 		}
		return Optional.empty();
	}

	@Deprecated
	private static ImmutableList<Object> metaValues(Site site, String key) {
		ImmutableList<Object> dates = site.blobs().stream()
			.map(blob -> blob.meta().find(Object.class, key))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.collect(ImmutableList.toImmutableList());
		return dates;
	}
	
}
