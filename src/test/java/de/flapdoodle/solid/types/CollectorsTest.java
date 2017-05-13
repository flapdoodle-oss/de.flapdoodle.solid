package de.flapdoodle.solid.types;

import static org.junit.Assert.assertEquals;

import java.util.stream.Stream;

import org.junit.Test;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;

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
}
