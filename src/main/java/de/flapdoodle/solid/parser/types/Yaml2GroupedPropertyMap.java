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
package de.flapdoodle.solid.parser.types;

import java.util.Map;

import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.parser.meta.Yaml;
import de.flapdoodle.solid.types.GroupedPropertyMap;
import de.flapdoodle.solid.types.ImmutableGroupedPropertyMap.Builder;

public class Yaml2GroupedPropertyMap implements AsGroupedPropertyMap<Yaml> {

	@Override
	public GroupedPropertyMap asGroupedPropertyMap(Yaml source) {
		Builder builder = GroupedPropertyMap.builder();
		fill(builder, ImmutableList.of(), source.asMap());
		return builder.build();
	}

	private void fill(Builder builder, ImmutableList<String> currentKey, Map<String, Object> map) {
		map.forEach((key, value) -> {
			if (value instanceof Map) {
				fill(builder,key(currentKey,key),(Map<String, Object>) value);
			} else {
				builder.put(key(currentKey,key), value);
			}
		});
	}

	private static ImmutableList<String> key(ImmutableList<String> current, String key) {
		return ImmutableList.<String>builder()
				.addAll(current)
				.add(key)
				.build();
	}
}
