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
package de.flapdoodle.solid.parser.config;

import java.util.Map;

import com.moandjiezana.toml.Toml;

import de.flapdoodle.solid.types.ImmutablePropertyTreeMap;
import de.flapdoodle.solid.types.ImmutablePropertyTreeMap.MapBuilder;
import de.flapdoodle.solid.types.PropertyTreeMap;

public class Toml2PropertyTreeMap implements AsPropertyTreeMap<Toml> {

	@Override
	public PropertyTreeMap asPropertyTreeMap(Toml source) {
		MapBuilder builder = ImmutablePropertyTreeMap.builder();
		fill(builder, source.toMap());
		return builder.build();
	}

	private void fill(MapBuilder builder, Map<String, Object> map) {
		map.forEach((key, value) -> {
			if (value instanceof Map) {
				fill(builder.start(key),(Map<String, Object>) value);
			} else {
				builder.put(key, value);
			}
		});
	}

}
