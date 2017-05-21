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
package de.flapdoodle.solid.site;

import org.immutables.value.Value;
import org.immutables.value.Value.Default;
import org.immutables.value.Value.Style;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.flapdoodle.legacy.Optionals;
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
	
	String theme();
	
	ImmutableMap<String, String> properties();
	
	Urls urls();
	
	PathProperties pathProperties();
	
	PostProcessing postProcessing();
	
	Formatters formatters();
	
	Filters filters();
	
	ImmutableList<String> defaultOrdering();
	
	ImmutableMap<String, String> defaultFormatter();
	
	public static ImmutableSiteConfig.Builder builder() {
		return ImmutableSiteConfig.builder();
	}
	
	public static SiteConfig of(String filename, PropertyTree map) {
		Builder builder = builder()
				.filename(filename)
				.baseUrl(map.find(String.class, "baseURL").get())
				.theme(Optionals.checkPresent(map.find(String.class, "theme"),"theme not set in %s",filename).get());
		
		map.find(String.class, "title")
			.ifPresent(v -> builder.putProperties("title", v));
		map.find(String.class, "subtitle")
			.ifPresent(v -> builder.putProperties("subtitle", v));
		
		builder.urls(Urls.of(Optionals.checkPresent(map.find("urls"),"urls not found in %s",map).get()));
		
		builder.pathProperties(map.find("pathProperties")
				.map(PathProperties::of)
				.orElse(() -> PathProperties.empty()));

		builder.postProcessing(map.find("postProcessing")
			.map(PostProcessing::of)
			.orElse(() -> PostProcessing.empty()));
		
		builder.formatters(map.find("formatters")
				.map(Formatters::of)
				.orElse(() -> Formatters.empty()));
		
		builder.filters(map.find("filters")
				.map(Filters::of)
				.orElse(() -> Filters.empty()));
		
		builder.addAllDefaultOrdering(map.findList(String.class, "order"));
		
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
