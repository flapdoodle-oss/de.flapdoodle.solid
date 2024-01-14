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
package de.flapdoodle.solid.types.paging;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import de.flapdoodle.solid.types.paging.Pager.KeyValue;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class PagerTest {

	@Test
	public void emptyCollectionDoesNotCallConsumer() {
		Pager.forEach(ImmutableList.of(), (last,current,next) -> { throw new RuntimeException("not called");});
	}

	@Test
	public void oneElementShouldGive1Call() {
		List<String> calls=Lists.newArrayList();
		Pager.forEach(ImmutableList.of("one"), (last,current,next) -> {
			assertFalse(last.isPresent());
			assertFalse(next.isPresent());
			calls.add(current);
		});
		assertEquals(1, calls.size());
		assertEquals("one", calls.get(0));
	}

	@Test
	public void twoElementsShouldGive2Calls() {
		List<String> calls=Lists.newArrayList();
		Pager.forEach(ImmutableList.of("one","two"), (last,current,next) -> {
			if (current.equals("one")) {
				assertFalse(last.isPresent());
				assertEquals("two", next.get());
			}
			if (current.equals("two")) {
				assertEquals("one", last.get());
				assertFalse(next.isPresent());
			}
			calls.add(current);
		});
		assertEquals(2, calls.size());
		assertEquals("one", calls.get(0));
		assertEquals("two", calls.get(1));
	}
	
	@Test
	public void threeElementsShouldGive3Calls() {
		List<String> calls=Lists.newArrayList();
		Pager.forEach(ImmutableList.of("one","two","3"), (last,current,next) -> {
			if (current.equals("one")) {
				assertFalse(last.isPresent());
				assertEquals("two", next.get());
			}
			if (current.equals("two")) {
				assertEquals("one", last.get());
				assertEquals("3", next.get());
			}
			if (current.equals("3")) {
				assertEquals("two", last.get());
				assertFalse(next.isPresent());
			}
			calls.add(current);
		});
		assertEquals(3, calls.size());
		assertEquals("one", calls.get(0));
		assertEquals("two", calls.get(1));
		assertEquals("3", calls.get(2));
	}
	
	@Test
	public void mapShouldWorkToo() {
		List<KeyValue<String, Integer>> calls=Lists.newArrayList();
		ImmutableMap<String, Integer> map=ImmutableMap.of("one",1,"two",2,"3",3);
		Pager.forEach(map, (last,current,next) -> {
			if (current.key().equals("one")) {
				assertFalse(last.isPresent());
				assertEquals("two", next.get().key());
			}
			if (current.key().equals("two")) {
				assertEquals("one", last.get().key());
				assertEquals("3", next.get().key());
			}
			if (current.key().equals("3")) {
				assertEquals("two", last.get().key());
				assertFalse(next.isPresent());
			}
			calls.add(current);
		});
		assertEquals(3, calls.size());
		assertEquals("one", calls.get(0).key());
		assertEquals("two", calls.get(1).key());
		assertEquals("3", calls.get(2).key());
	}
}
