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
package de.flapdoodle.solid.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class ImmutableGroupedPropertyMapTest {

	@Test
	public void simplePut() {
		ImmutableGroupedPropertyMap map = ImmutableGroupedPropertyMap.builder()
			.put("foo", "bar")
			.build();
		
		assertEquals("bar", map.get("foo").get());
		
		ImmutableMap<String, Object> properties = map.propertiesOf();
		assertEquals(ImmutableMap.of("foo","bar"), properties);
	}
	
	@Test
	public void multipleProperties() {
		ImmutableGroupedPropertyMap map = ImmutableGroupedPropertyMap.builder()
			.put("foo", "bar")
			.put("bar", "foo")
			.build();
		
		assertEquals("bar", map.get("foo").get());
		assertEquals("foo", map.get("bar").get());
		
		ImmutableMap<String, Object> properties = map.propertiesOf();
		assertEquals(ImmutableMap.of("foo","bar","bar","foo"), properties);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void overwriteMustFail() {
		ImmutableGroupedPropertyMap.builder()
			.put("foo", "bar")
			.put("foo", "blob")
			.build();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void overwriteMustFailInGroup() {
		ImmutableGroupedPropertyMap.builder()
			.put("foo","sub", "bar")
			.put("foo","sub", "blob")
			.build();
	}
	
	@Test
	public void getNothingIfKeyDoesNotExistPut() {
		ImmutableGroupedPropertyMap map = ImmutableGroupedPropertyMap.builder()
			.put("foo", "bar")
			.build();
		
		assertFalse(map.get("bar").isPresent());
		assertFalse(map.get("bar","foo").isPresent());
	}
	
	@Test
	public void nestedPut() {
		ImmutableGroupedPropertyMap map = ImmutableGroupedPropertyMap.builder()
			.put("foo","blob", "bar")
			.build();
		
		
		assertEquals("bar", map.get("foo","blob").get());
		
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
