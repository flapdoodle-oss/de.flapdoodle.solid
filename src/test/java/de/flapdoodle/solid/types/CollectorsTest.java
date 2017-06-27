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

import java.util.stream.Stream;

import org.junit.Test;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

public class CollectorsTest {

	private static final int GENERATED_ITEMS = 10000;

	@Test
	public void immutableMultiMapCollector() {
		ImmutableMultimap<Integer, Integer> result = Stream.iterate(0, p -> p+1)
				.parallel()
				.limit(GENERATED_ITEMS)
				.collect(Collectors.groupingBy(i -> i % 10));
		
		assertEquals(GENERATED_ITEMS,result.size());
		assertEquals(10,result.asMap().size());
		for (int i=0;i<10;i++) {
			int key=i;
			ImmutableCollection<Integer> values = result.get(key);
			values.forEach(v -> {
				assertEquals(key,v % 10);
			});
		}
	}
	
	@Test
	public void immutableMultiMapValuesCollector() {
		ImmutableMultimap<Character, String> result = Stream.of("aaa","bbb","ccc","abc","bcd")
				.parallel()
				.collect(Collectors.groupingByValues((String s) -> ImmutableSet.of(s.charAt(0),s.charAt(1))));
		
		assertEquals(7,result.size());
		assertEquals(3,result.asMap().size());
		assertEquals("[aaa, abc]",result.asMap().get('a').toString());
		assertEquals("[bbb, abc, bcd]",result.asMap().get('b').toString());
		assertEquals("[ccc, bcd]",result.asMap().get('c').toString());
	}
}
