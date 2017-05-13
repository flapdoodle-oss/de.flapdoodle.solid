package de.flapdoodle.solid.types;

import java.util.function.Function;
import java.util.stream.Collector;

import org.immutables.value.Value.Immutable;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedListMultimap;

public class Collectors {

	public static <T, K> Collector<T, ?, ImmutableMultimap<K, T>> groupingBy(Function<? super T, ? extends K> classifier) {
		return ImmutableGenericCollector.<T, LinkedListMultimap<K, T>, ImmutableMultimap<K, T>>builder()
			.supplier(LinkedListMultimap::create)
			.accumulator((map, t) -> {
				map.put(classifier.apply(t), t);
			})
			.combiner((a,b) -> {
				LinkedListMultimap<K, T> ret = LinkedListMultimap.create(a);
				ret.putAll(b);
				return ret;
			})
			.finisher(map -> ImmutableMultimap.copyOf(map))
			.build();
	}

	@Immutable
	static abstract class GenericCollector<T, A, R> implements Collector<T, A, R> {
		
	}
}
