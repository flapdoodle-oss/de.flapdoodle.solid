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
package de.flapdoodle.solid.types.properties;

import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public class MapBasedTypePropertiesLookup implements TypePropertiesLookup {

	private final ImmutableMap<Class<?>, TypeProperties<?>> map;

	private MapBasedTypePropertiesLookup(Map<Class<?>, TypeProperties<?>> map) {
		this.map = ImmutableMap.copyOf(map);
	}
	
	@Override
	public <T> Optional<TypeProperties<T>> propertiesOf(Class<T> type) {
		Optional<TypeProperties<T>> ret = Optional.ofNullable((TypeProperties<T>) map.get(type));
		if (!ret.isPresent()) {
			Class<?> assignableType = assignableType(map.keySet(), type);
			return Optional.ofNullable((TypeProperties<T>) map.get(assignableType));
		}
		return ret;
	}

	private static Class<?> assignableType(ImmutableSet<Class<?>> keySet, Class<?> type) {
		for (Class<?> t : keySet) {
			if (t.isAssignableFrom(type)) {
				return t;
			}
		};
		return type;
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private final Map<Class<?>, TypeProperties<?>> map=Maps.newLinkedHashMap();

		public <T> Builder add(Class<T> type, TypeProperties<T> properties) {
			map.put(type, properties);
			return this;
		}
		
		public MapBasedTypePropertiesLookup build() {
			return new MapBasedTypePropertiesLookup(map);
		}
	}
}
