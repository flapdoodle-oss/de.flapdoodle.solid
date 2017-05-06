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
package de.flapdoodle.solid.types.maps;

import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.flapdoodle.solid.types.Types;

public interface GroupedPropertyMap {

	Optional<Object> find(String ... key);
	
	default <T> Optional<T> find(Class<T> type, String ... key) {
		return find(key).flatMap(Types.isInstance(type));
	}

	ImmutableMap<String, Object> propertiesOf(String ... group);

	ImmutableSet<String> groupsOf(String ... group);
	
	default String prettyPrinted() {
		return GroupedPropertyMapPrinter.prettyPrinted(this);
	}

	public static ImmutableGroupedPropertyMap.Builder builder() {
		return ImmutableGroupedPropertyMap.builder();
	}
}
