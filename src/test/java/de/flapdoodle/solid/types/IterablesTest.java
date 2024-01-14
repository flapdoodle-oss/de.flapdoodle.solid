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
package de.flapdoodle.solid.types;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IterablesTest {

	@Test
	public void combinationOfNothingIsNothing() {
		ImmutableList<Pair<String, String>> result = Iterables.everyCombinationOf(ImmutableSet.<String>of());
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void combinationOfOneIsNothing() {
		ImmutableList<Pair<String, String>> result = Iterables.everyCombinationOf(ImmutableSet.<String>of("foo"));
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void combinationOfTwoIsOnePair() {
		ImmutableList<Pair<String, String>> result = Iterables.everyCombinationOf(ImmutableSet.<String>of("foo","bar"));
		assertEquals(1,result.size());
		assertEquals(Pair.of("foo", "bar"), result.get(0));
	}
	
	@Test
	public void combinationOf3AreTwoPair() {
		ImmutableList<Pair<String, String>> result = Iterables.everyCombinationOf(ImmutableSet.<String>of("foo","bar","baz"));
		assertEquals(3,result.size());
		assertEquals(Pair.of("foo", "bar"), result.get(0));
		assertEquals(Pair.of("foo", "baz"), result.get(1));
		assertEquals(Pair.of("bar", "baz"), result.get(2));
	}
}
