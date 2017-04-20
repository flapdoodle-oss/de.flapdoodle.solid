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

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class ImmutablePropertyTreeMapTest {

	@Test
	public void simplePut() {
		ImmutableMap<String, Object> result = ImmutablePropertyTreeMap.builder()
			.put("foo", "bar")
			.build().asMap();
		
		assertEquals(ImmutableMap.of("foo","bar"),result);
	}
	
	@Test
	public void simpleTree() {
		ImmutableMap<String, Object> result = ImmutablePropertyTreeMap.builder()
			.put("foo", "bar")
			.start("blob")
			.end()
			.build().asMap();
		
		assertEquals(ImmutableMap.of("foo","bar","blob",ImmutableMap.of()),result);
	}

	@Test
	public void putAList() {
		ImmutableMap<String, Object> result = ImmutablePropertyTreeMap.builder()
			.put("foo", Lists.newArrayList("12",3,3d,'c',Lists.newArrayList("subsub")))
			.build().asMap();
		
		assertEquals(ImmutableMap.of("foo",ImmutableList.of("12",3,3d,'c',ImmutableList.of("subsub"))),result);
	}
}
