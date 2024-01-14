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
package de.flapdoodle.solid.types.maps;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ImmutableGroupedPropertyMapTest {

	@Test
	public void simplePut() {
		ImmutableGroupedPropertyMap map = ImmutableGroupedPropertyMap.builder()
			.put("foo", "bar")
			.build();
		
		assertEquals("bar", map.find("foo").get());
		
		ImmutableMap<String, Object> properties = map.propertiesOf();
		assertEquals(ImmutableMap.of("foo","bar"), properties);
	}
	
	@Test
	public void multipleProperties() {
		ImmutableGroupedPropertyMap map = ImmutableGroupedPropertyMap.builder()
			.put("foo", "bar")
			.put("bar", "foo")
			.build();
		
		assertEquals("bar", map.find("foo").get());
		assertEquals("foo", map.find("bar").get());
		
		ImmutableMap<String, Object> properties = map.propertiesOf();
		assertEquals(ImmutableMap.of("foo","bar","bar","foo"), properties);
	}
	
	@Test
	public void overwriteMustFail() {
		assertThatThrownBy(() -> ImmutableGroupedPropertyMap.builder()
			.put("foo", "bar")
			.put("foo", "blob")
			.build()).isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void overwriteMustFailInGroup() {
		assertThatThrownBy(() -> ImmutableGroupedPropertyMap.builder()
			.put("foo","sub", "bar")
			.put("foo","sub", "blob")
			.build()).isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void getNothingIfKeyDoesNotExistPut() {
		ImmutableGroupedPropertyMap map = ImmutableGroupedPropertyMap.builder()
			.put("foo", "bar")
			.build();
		
		assertFalse(map.find("bar").isPresent());
		assertFalse(map.find("bar","foo").isPresent());
	}
	
	@Test
	public void nestedPut() {
		ImmutableGroupedPropertyMap map = ImmutableGroupedPropertyMap.builder()
			.put("foo","blob", "bar")
			.build();
		
		
		assertEquals("bar", map.find("foo","blob").get());
		
		ImmutableMap<String, Object> properties = map.propertiesOf();
		assertEquals(ImmutableMap.of(), properties);
		
		properties = map.propertiesOf("foo");
		assertEquals(ImmutableMap.of("blob","bar"), properties);
		
		ImmutableSet<String> groups = map.groupsOf();
		assertEquals(ImmutableSet.of("foo"), groups);
		
		groups = map.groupsOf("foo");
		assertEquals(ImmutableSet.of(), groups);
	}
}
