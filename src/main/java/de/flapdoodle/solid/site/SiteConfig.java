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

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import de.flapdoodle.solid.site.ImmutableSiteConfig.Builder;
import de.flapdoodle.solid.types.GroupedPropertyMap;
import de.flapdoodle.solid.types.Types;

@Value.Immutable
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
	
	Optional<String> theme();
	
	ImmutableMap<String, String> properties();
	
	public static ImmutableSiteConfig.Builder builder() {
		return ImmutableSiteConfig.builder();
	}
	
	public static SiteConfig of(String filename, GroupedPropertyMap map) {
		Builder builder = builder()
				.filename(filename)
				.baseUrl(map.get("baseURL").flatmap(v -> Types.isInstance(String.class, v)).get())
				.theme(map.get("theme").flatmap(v -> Types.isInstance(String.class, v)).asGuava());
		
		map.get("title").flatmap(v -> Types.isInstance(String.class, v))
			.ifPresent(v -> builder.putProperties("title", v));
		map.get("subtitle").flatmap(v -> Types.isInstance(String.class, v))
				.ifPresent(v -> builder.putProperties("subtitle", v));
		
		return builder.build();
	}
}
