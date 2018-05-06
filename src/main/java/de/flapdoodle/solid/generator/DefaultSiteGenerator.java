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
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;

import de.flapdoodle.solid.generator.PathRenderer.FormatterOfProperty;
import de.flapdoodle.solid.parser.content.Blob;
import de.flapdoodle.solid.parser.content.Blobs;
import de.flapdoodle.solid.parser.content.Site;
import de.flapdoodle.solid.parser.content.Sites;
import de.flapdoodle.solid.parser.path.Path;
import de.flapdoodle.solid.site.Paging;
import de.flapdoodle.solid.site.PathProperties;
import de.flapdoodle.solid.site.Urls.Config;
import de.flapdoodle.solid.theme.Context;
import de.flapdoodle.solid.theme.DefaultLinkFactory;
import de.flapdoodle.solid.theme.LinkFactories;
import de.flapdoodle.solid.theme.Page;
import de.flapdoodle.solid.theme.Paths;
import de.flapdoodle.solid.theme.Renderer;
import de.flapdoodle.solid.types.Collectors;
import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.solid.types.paging.Pager;
import de.flapdoodle.solid.types.paging.Pager.KeyValue;

public class DefaultSiteGenerator implements SiteGenerator {

	private final PropertyCollectionResolver propertyCollectionResolver;
	private final Function<Site, PathRenderer> pathRendererFactory;
	private final FilterFactory filterFactory;

	public DefaultSiteGenerator(PropertyCollectionResolver propertyCollectionResolver, Function<Site, PathRenderer> pathRendererFactory, FilterFactory filterFactory) {
		this.propertyCollectionResolver = propertyCollectionResolver;
		this.pathRendererFactory = pathRendererFactory;
		this.filterFactory = filterFactory;
	}
	
	@Override
	public ImmutableList<Document> generate(Site site) {
		System.out.println(" blobs: -> "+site.blobs().size());
		System.out.println(" theme: -> "+site.theme());
		
//		System.out.println("dates -> "+metaValues(site, "date"));
//		System.out.println("titles -> "+metaValues(site, "title"));
		
		ImmutableList.Builder<Document> documents=ImmutableList.builder();
		
		FormatterOfProperty propertyFormatter=Sites.formatterOfProperty(site);
		
		final ImmutableList<String> defaultOrdering=site.config().defaultOrdering().isEmpty() 
				? ImmutableList.of("!date")
				: site.config().defaultOrdering();
		
		Paging paging = site.config().paging().isPresent()
				? site.config().paging().get()
				: Paging.Reversed;
				
		ImmutableMap.Builder<String, GroupedBlobs> groupedBlobsBuilder=ImmutableMap.builder();
				
		PathProperties pathProperties = site.config().pathProperties().merge(PathProperties.defaults());
		site.config().urls().configs().forEach((String name, Config config) -> {
			Path currentPath = config.path();
			
			ImmutableList<String> currentOrdering=config.ordering().isEmpty() 
					? defaultOrdering 
					: config.ordering();

			ImmutableList<Blob> sortedBlobs = Blobs.sort(site.blobs(), currentOrdering);
			
			ImmutableMultimap<ImmutableMap<String, Object>, Blob> groupedBlobs;
			if (!currentPath.isPagedEmpty()) {
				groupedBlobs = Blobs.filter(sortedBlobs, filterFactory.filters(config.filters(), site.config().filters().filters()))
						.stream()
						.collect(Collectors.groupingByValues(blob -> Blobs.pathPropertiesOf(blob, pathProperties::mapped, currentPath, propertyCollectionResolver)));
			} else {
				groupedBlobs=ImmutableMultimap.<ImmutableMap<String, Object>, Blob>builder()
						.putAll(ImmutableMap.of(), sortedBlobs)
						.build();
			}
			
			if (!config.pathOrdering().isEmpty()) {
				groupedBlobs=Blobs.orderByKey(groupedBlobs, config.pathOrdering());
			}

			if (currentPath.propertyNames().contains(Path.PAGE)) {
				groupedBlobs=Blobs.groupByPage(groupedBlobs, Path.PAGE, Maybe.fromOptional(config.itemsPerPage()).orElse(() -> 10));
			}
			
			if (config.paging().orElse(paging)==Paging.Reversed) {
				groupedBlobs=reverse(groupedBlobs);
			}
			
			groupedBlobsBuilder.put(name, GroupedBlobs.builder()
					.currentPath(currentPath)
					.putAllGroupedBlobs(groupedBlobs)
					.build());
		});
		
		ImmutableMap<String, GroupedBlobs> groupedBlobsById = groupedBlobsBuilder.build();
		
		PathRenderer pathRenderer = pathRendererFactory.apply(site);
		
		LinkFactories.Named linkFactory=DefaultLinkFactory.of(groupedBlobsById, pathRenderer, propertyFormatter);

		groupedBlobsById.forEach((name, grouped) -> {
			System.out.println(name);
			Path currentPath = grouped.currentPath();
			
			Pager.forEach(grouped.groupedBlobs().asMap(), isPageBreak(currentPath), (Maybe<KeyValue<ImmutableMap<String, Object>, Collection<Blob>>> before,KeyValue<ImmutableMap<String, Object>, Collection<Blob>> current,Maybe<KeyValue<ImmutableMap<String, Object>, Collection<Blob>>> after) -> {
				ImmutableMap<String, Object> key = current.key();
				Collection<Blob> blobs = current.value();
				
				String renderedPath = Maybe.isPresent(pathRenderer.render(currentPath, key, propertyFormatter),"could not render path for: %s with %s",currentPath,key).get();
				
				Maybe<Page> prev = pageOf(pathRenderer, currentPath, before, propertyFormatter);
				
				Maybe<Page> next=pageOf(pathRenderer, currentPath, after, propertyFormatter);
				
				System.out.println(" "+key+" -> "+blobs.size()+" --> "+renderedPath+"(prev:"+prev.isPresent()+",next:"+next.isPresent()+")");
				
				Content renderedResult = site.theme().rendererFor(name).render(Renderer.Renderable.builder()
						.addAllBlobs(blobs)
						.context(Context.builder()
								.putAllPathProperties(key)
								.site(site)
								.paths(new PathsImpl(renderedPath,prev,next))
								.linkFactory(linkFactory)
								.build())
						.build());
				
				documents.add(Document.builder()
					.path(renderedPath)
					.content(renderedResult)
					.build());
				
			});
		});
		
		documents.addAll(applyBaseUrl(site.config().baseUrl(), site.theme().staticFiles()));
		documents.addAll(applyBaseUrl(site.config().baseUrl(), site.staticFiles()));
		
		return documents.build();
	}

	private BiFunction<Entry<ImmutableMap<String, Object>, Collection<Blob>>, Entry<ImmutableMap<String, Object>, Collection<Blob>>, Boolean> isPageBreak(Path path) {
		if (!path.isPaging()) {
			return (a,b) -> false;
		}
			
		return (a,b) -> {
			ImmutableMap<String, Object> keysA = a.getKey();
			ImmutableMap<String, Object> keysB = b.getKey();
			
			
			Map<String, ValueDifference<Object>> diff = Maps.difference(keysA, keysB).entriesDiffering();
			Set<String> keySet = diff.keySet();
			
			boolean onlyDifferentPage = keySet.size()==1 && keySet.contains(Path.PAGE);
			
			return !onlyDifferentPage;
		};
	}


	private ImmutableMultimap<ImmutableMap<String, Object>, Blob> reverse(ImmutableMultimap<ImmutableMap<String, Object>, Blob> src) {
		ImmutableMultimap.Builder<ImmutableMap<String,Object>, Blob> builder=ImmutableMultimap.builder();
		src.keySet().asList().reverse().forEach(key -> {
			builder.putAll(key, src.get(key));
		});
		return builder.build();
	}

	private Iterable<? extends Document> applyBaseUrl(String baseUrl, ImmutableList<Document> src) {
		return src.stream()
			.map(d -> ImmutableDocument.copyOf(d).withPath(baseUrl+d.path()))
			.collect(ImmutableList.toImmutableList());
	}

	private static Maybe<Page> pageOf(PathRenderer pathRenderer, Path currentPath, Maybe<KeyValue<ImmutableMap<String, Object>, Collection<Blob>>> pageBlobs, FormatterOfProperty propertyFormatter) {
		Maybe<String> title = pageBlobs.map(KeyValue::value)
			.flatMap(blobs -> {
				return blobs.size()==1 ? blobs.iterator().next().meta()
						.find(String.class, "title") : Maybe.<String>absent();
			});
		
		return pageBlobs.map(KeyValue::key)
				.flatMap(k -> pathRenderer.render(currentPath, k, propertyFormatter))
				.map(url -> Page.builder()
						.url(url)
						.title(title.asOptional())
						.build());
	}

	@Deprecated
	private static ImmutableList<Object> metaValues(Site site, String key) {
		ImmutableList<Object> dates = site.blobs().stream()
			.map(blob -> blob.meta().find(Object.class, key))
			.filter(Maybe::isPresent)
			.map(Maybe::get)
			.collect(ImmutableList.toImmutableList());
		return dates;
	}
	
	private class PathsImpl implements Paths {

		private final String currentUrl;
		private final Optional<Page> prev;
		private final Optional<Page> next;
		
		public PathsImpl(String currentUrl, Maybe<Page> prev, Maybe<Page> next) {
			this.currentUrl = currentUrl;
			this.prev = prev.asOptional();
			this.next = next.asOptional();
		}

		@Override
		public String currentUrl() {
			return currentUrl;
		}

		@Override
		public Optional<Page> getPreviousPage() {
			return prev;
		}

		@Override
		public Optional<Page> getNextPage() {
			return next;
		}

	}
}
