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
import com.google.common.collect.ImmutableMultimap;

public class MultimapsTest {

	@Test
	public void flattenSimpleSample() {
		ImmutableList<ImmutableMap<String, Integer>> result = Multimaps.flatten(ImmutableMultimap.<String, Integer>builder()
				.putAll("a", 1)
				.putAll("b", 2)
				.build());
		
		assertEquals(1,result.size());
		assertEquals("[{a=1, b=2}]",result.toString());
	}

	@Test
	public void flattenSampleWithOneCollection() {
		ImmutableList<ImmutableMap<String, Integer>> result = Multimaps.flatten(ImmutableMultimap.<String, Integer>builder()
				.putAll("a", 1)
				.putAll("b", 2,3)
				.build());
		
		assertEquals(2,result.size());
		assertEquals("[{a=1, b=2}, {a=1, b=3}]",result.toString());
	}
	
	@Test
	public void flattenSampleWithTwoCollections() {
		ImmutableList<ImmutableMap<String, Integer>> result = Multimaps.flatten(ImmutableMultimap.<String, Integer>builder()
				.putAll("a", 1,4)
				.putAll("b", 2,3)
				.build());
		
		assertEquals(4,result.size());
		assertEquals("[{a=1, b=2}, {a=4, b=2}, {a=1, b=3}, {a=4, b=3}]",result.toString());
	}
	
	@Test
	public void flattenSampleWith3() {
		ImmutableList<ImmutableMap<String, Integer>> result = Multimaps.flatten(ImmutableMultimap.<String, Integer>builder()
				.putAll("a", 1,4)
				.putAll("m", 0)
				.putAll("b", 2,3)
				.build());
		
		assertEquals(4,result.size());
		assertEquals("[{a=1, m=0, b=2}, {a=4, m=0, b=2}, {a=1, m=0, b=3}, {a=4, m=0, b=3}]",result.toString());
	}
}
