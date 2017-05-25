package de.flapdoodle.solid.types.paging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

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
}
