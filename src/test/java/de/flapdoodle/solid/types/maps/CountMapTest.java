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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

public class CountMapTest {

	@Test
	public void oneEntryGivesEmptyMap() {
		ImmutableMap<String, Integer> result = CountMap.scale(ImmutableMap.of("foo",133), 3, 7);
		assertTrue(result.isEmpty());
	}

	@Test
	public void scaleWithSameValueGivesEmptyMap() {
		ImmutableMap<String, Integer> result = CountMap.scale(ImmutableMap.of("foo",100,"bar",100), 3, 7);
		assertTrue(result.isEmpty());
	}

	@Test
	public void scaleMinMaxWithTwoEntries() {
		ImmutableMap<String, Integer> result = CountMap.scale(ImmutableMap.of("foo",100,"bar",200), 3, 7);
		assertEquals(3,result.get("foo").intValue());
		assertEquals(7,result.get("bar").intValue());
	}

	@Test
	public void sampleScale() {
		ImmutableMap<String, Integer> result = CountMap.scale(ImmutableMap.of("foo",100,"bar",200,"baz",400), 1, 4);
		assertEquals(1,result.get("foo").intValue());
		assertEquals(2,result.get("bar").intValue());
		assertEquals(4,result.get("baz").intValue());
	}
}
