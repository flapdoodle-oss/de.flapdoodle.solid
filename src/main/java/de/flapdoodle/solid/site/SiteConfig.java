/*
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
package de.flapdoodle.solid.site;

import java.util.Optional;

import org.immutables.value.Value;
import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Style;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.flapdoodle.legacy.Optionals;
import de.flapdoodle.solid.parser.Tree;
import de.flapdoodle.solid.site.ImmutableSiteConfig.Builder;
import de.flapdoodle.solid.types.tree.PropertyTree;

@Value.Immutable
@Style(deepImmutablesDetection=true)
public interface SiteConfig {

	String filename();

	String baseUrl();

	@Default
	default String contentDirectory() {
		return "content";
	}

	@Default
	default String staticDirectory() {
		return "static";
	}
	
	@Default
	default boolean relativeLinks() {
		return false;
	}
	
	@Default
	default boolean enableDisqus() {
		return true;
	}

	String theme();

	ImmutableMap<String, String> properties();
	Optional<PropertyTree> tree();

	@Auxiliary
	default Optional<Tree> tree(String id) {
		return tree()
				.flatMap(tree -> tree.find(id).asOptional())
				.map(tree -> Tree.treeOf(tree));
	}


	Urls urls();

	PathProperties pathProperties();

	PostProcessing postProcessing();

	UrlRewrite urlRewrite();

	Formatters formatters();

	Filters filters();

	ImmutableList<String> defaultOrdering();

	Optional<Paging> paging();

	ImmutableMap<String, String> defaultFormatter();

	public static ImmutableSiteConfig.Builder builder() {
		return ImmutableSiteConfig.builder();
	}

	public static SiteConfig of(String filename, PropertyTree map) {
		String baseUrl = map.find(String.class, "baseURL").get();
		
		Preconditions.checkArgument(baseUrl.endsWith("/"),"baseUrl does not end with '/' -> %s",baseUrl);
		
		Builder builder = builder()
				.filename(filename)
				.baseUrl(baseUrl)
				.theme(Optionals.checkPresent(map.find(String.class, "theme"),"theme not set in %s",filename).get());
		
		map.find(Boolean.class,"relativeLinks")
			.ifPresent(v -> builder.relativeLinks(v));
		map.find(Boolean.class,"enableDisqus")
			.ifPresent(v -> builder.enableDisqus(v));

		ImmutableSet<String> validProperties=ImmutableSet.of("title","subtitle");

		map.properties()
			.stream()
			.filter(validProperties::contains)
			.forEach(p -> {
			map.find(String.class, p)
				.ifPresent(v -> builder.putProperties(p, v));
		});

		builder.tree(map.find("tree").asOptional());

//		map.find(String.class, "title")
//			.ifPresent(v -> builder.putProperties("title", v));
//		map.find(String.class, "subtitle")
//			.ifPresent(v -> builder.putProperties("subtitle", v));

		builder.urls(Urls.of(Optionals.checkPresent(map.find("urls"),"urls not found in %s",map).get()));

		builder.pathProperties(map.find("pathProperties")
				.map(PathProperties::of)
				.orElse(() -> PathProperties.empty()));

		builder.postProcessing(map.find("postProcessing")
				.map(PostProcessing::of)
				.orElse(() -> PostProcessing.empty()));

		builder.urlRewrite(map.find("urlRewrite")
				.map(UrlRewrite::of)
				.orElse(() -> UrlRewrite.empty()));

		builder.formatters(map.find("formatters")
				.map(Formatters::of)
				.orElse(() -> Formatters.empty()));

		builder.filters(map.find("filters")
				.map(Filters::of)
				.orElse(() -> Filters.empty()));

		builder.addAllDefaultOrdering(map.findList(String.class, "order"));

		map.find(String.class, "staticContent")
			.ifPresent(s -> builder.staticDirectory(s));

		map.find("defaultFormatters")
			.ifPresent(def -> {
				def.properties().forEach(property -> {
					String formatter = Optionals.checkPresent(def.find(String.class, property),"invalid default formatter for %s",property).get();
					builder.putDefaultFormatter(property, formatter);
				});
			});

		return builder.build();
	}
}
