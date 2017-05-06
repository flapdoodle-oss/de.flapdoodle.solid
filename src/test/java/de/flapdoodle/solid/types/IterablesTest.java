package de.flapdoodle.solid.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

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
