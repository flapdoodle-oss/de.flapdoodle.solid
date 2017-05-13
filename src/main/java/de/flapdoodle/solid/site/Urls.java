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

import java.util.Optional;

import org.immutables.value.Value.Immutable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.flapdoodle.legacy.Optionals;
import de.flapdoodle.solid.parser.path.Path;
import de.flapdoodle.solid.types.tree.PropertyTree;

@Immutable
public interface Urls {
	ImmutableMap<String, Config> configs();

	@Immutable
	interface Config {
		Path path();
		Optional<Integer> itemsPerPage();
		ImmutableSet<String> filters();
		
		public static ImmutableConfig.Builder builder() {
			return ImmutableConfig.builder();
		}
	}
	
	public static ImmutableUrls.Builder builder() {
		return ImmutableUrls.builder();
	}
	
	public static Urls of(PropertyTree urls) {
		ImmutableUrls.Builder urlsBuilder = Urls.builder();
		urls.properties().forEach(label -> {
			ImmutableConfig.Builder configBuilder = Urls.Config.builder();
			PropertyTree config = urls.find(label).get();
			String path = Optionals.checkPresent(config.find(String.class, "path"),"could not get propery path from %s in %s",config,label).get();
			configBuilder.path(Path.parse(path))
				.addAllFilters(config.findList(String.class, "filter"));
			urlsBuilder.putConfigs(label, configBuilder.build());
		});
		return urlsBuilder.build();
	}
}