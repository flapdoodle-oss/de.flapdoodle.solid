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

import org.immutables.value.Value.Immutable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;

import de.flapdoodle.solid.generator.PathRenderer.FormatterOfProperty;
import de.flapdoodle.solid.parser.content.Blob;
import de.flapdoodle.solid.parser.content.Blobs;
import de.flapdoodle.solid.parser.content.Site;
import de.flapdoodle.solid.parser.content.Sites;
import de.flapdoodle.solid.parser.path.Path;
import de.flapdoodle.solid.site.PathProperties;
import de.flapdoodle.solid.site.Urls.Config;
import de.flapdoodle.solid.theme.Context;
import de.flapdoodle.solid.theme.Paths;
import de.flapdoodle.solid.theme.Renderer;
import de.flapdoodle.solid.types.Collectors;
import de.flapdoodle.solid.types.Maybe;

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
		
		FormatterOfProperty propertyFormatter=Sites.formatterOfProperty(site);
		
		final ImmutableList<String> defaultOrdering=site.config().defaultOrdering().isEmpty() 
				? ImmutableList.of("!date")
				: site.config().defaultOrdering();
				
		ImmutableMap.Builder<String, GroupedBlobs> groupedBlobsBuilder=ImmutableMap.builder();
				
		PathProperties pathProperties = site.config().pathProperties().merge(PathProperties.defaults());
		site.config().urls().configs().forEach((String name, Config config) -> {
			Path currentPath = config.path();
			
			ImmutableList<String> currentOrdering=config.ordering().isEmpty() 
					? defaultOrdering 
					: config.ordering();

			ImmutableList<Blob> sortedBlobs = Blobs.sort(site.blobs(), currentOrdering);
			
			ImmutableMultimap<ImmutableMap<String, Object>, Blob> groupedBlobs = Blobs.filter(sortedBlobs, filterFactory.filters(config.filters(), site.config().filters().filters())).stream()
				.collect(Collectors.groupingBy(blob -> Blobs.pathPropertiesOf(blob, pathProperties::mapped, currentPath, propertyResolver)));

			if (currentPath.propertyNames().contains(Path.PAGE)) {
				groupedBlobs=Blobs.groupByPage(groupedBlobs, Path.PAGE, Maybe.fromOptional(config.itemsPerPage()).orElse(() -> 10));
			}
			
			groupedBlobsBuilder.put(name, GroupedBlobs.builder()
					.currentPath(currentPath)
					.putAllGroupedBlobs(groupedBlobs)
					.build());
		});
		
		ImmutableMap<String, GroupedBlobs> groupedBlobsById = groupedBlobsBuilder.build();
		groupedBlobsById.forEach((name, grouped) -> {
			System.out.println(name);
			grouped.groupedBlobs().asMap().forEach((key, blobs) -> {
				Path currentPath = grouped.currentPath();
				String renderedPath = Maybe.isPresent(pathRenderer.render(currentPath, key, propertyFormatter),"could not render path for: %s with %s",currentPath,key).get();
				
				System.out.println(" "+key+" -> "+blobs.size()+" --> "+renderedPath);
				Content renderedResult = site.theme().rendererFor(name).render(Renderer.Renderable.builder()
						.addAllBlobs(blobs)
						.context(Context.builder()
								.putAllPathProperties(key)
								.site(site)
								.paths(new PathsImpl(renderedPath))
								.build())
						.build());
				
				documents.add(Document.builder()
					.path(renderedPath)
					.content(renderedResult)
					.build());
			});
		});
		
		documents.addAll(site.theme().staticFiles());
		
		return documents.build();
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
	
	@Immutable
	static interface GroupedBlobs {
		
		Path currentPath();
		ImmutableMultimap<ImmutableMap<String, Object>, Blob> groupedBlobs();
		
		public static ImmutableGroupedBlobs.Builder builder() {
			return ImmutableGroupedBlobs.builder();
		}
	}
	
	private class PathsImpl implements Paths {

		private final String currentUrl;
		
		public PathsImpl(String currentUrl) {
			this.currentUrl = currentUrl;
		}

		@Override
		public String currentUrl() {
			return currentUrl;
		}
		
	}
}
