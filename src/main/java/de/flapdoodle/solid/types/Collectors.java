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

	public static <T, L, K> Collector<T, ?, ImmutableMultimap<K, T>> groupingByValues(Function<? super T, ? extends Iterable<? extends K>> classifier) {
		return ImmutableGenericCollector.<T, LinkedListMultimap<K, T>, ImmutableMultimap<K, T>>builder()
			.supplier(LinkedListMultimap::create)
			.accumulator((map, t) -> {
				classifier.apply(t).forEach(k -> {
					map.put(k, t);
				});
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
