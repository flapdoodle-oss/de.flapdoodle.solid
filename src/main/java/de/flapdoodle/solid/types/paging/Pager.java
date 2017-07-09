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
package de.flapdoodle.solid.types.paging;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import com.google.common.base.Preconditions;

import de.flapdoodle.solid.types.Maybe;

public interface Pager {
	
	public static <T> void forEach(Iterable<T> src, PagingConsumer<T> consumer) {
		forEach(src, (a,b) -> false, consumer);
	}
	
	public static <T> void forEach(Iterable<T> src, BiFunction<T, T, Boolean> isPageBreak, PagingConsumer<T> consumer) {
		T last=null;
		T current=null;
		T next=null;
		
		boolean first=true;
		for (T t : src) {
			last=current;
			current=next;
			next=Preconditions.checkNotNull(t,"element is null: %s",src);
			if (first) {
				first=false;
			} else {
				Maybe<T> prevPage = Maybe.ofNullable(last);
				Maybe<T> nextPage = Maybe.ofNullable(next);
				if (prevPage.isPresent()) {
					if (isPageBreak.apply(prevPage.get(), current)) {
						prevPage=Maybe.not();
					}
				}
				if (nextPage.isPresent()) {
					if (isPageBreak.apply(current, nextPage.get())) {
						nextPage=Maybe.not();
					}
				}
				consumer.accept(prevPage, current, nextPage);
			}
		}
		if (next!=null) {
			Maybe<T> prevPage = Maybe.ofNullable(current);
			consumer.accept(prevPage, next, Maybe.not());
		}
	}
	
	public static <K,V> void forEach(Map<K, V> src, PagingConsumer<KeyValue<K, V>> consumer) {
		forEach(src, (a,b) -> false, consumer);
	}
	
	public static <K,V> void forEach(Map<K, V> src, BiFunction<Entry<K, V>, Entry<K, V>, Boolean> isPageBreak, PagingConsumer<KeyValue<K, V>> consumer) {
		forEach(src.entrySet(), isPageBreak, (Maybe<Entry<K, V>> before,Entry<K, V> current,Maybe<Entry<K, V>> after) -> {
			consumer.accept(before.map(KeyValue::of), KeyValue.of(current), after.map(KeyValue::of));
		});
	}
	
	interface PagingConsumer<T> {
		void accept(Maybe<T> before, T current, Maybe<T> after);
	}
	
	@Immutable
	public static interface KeyValue<K,V> {
		@Parameter
		K key();
		@Parameter
		V value();
		
		public static <K,V> KeyValue<K, V> of(K key, V value) {
			return ImmutableKeyValue.of(key, value);
		}
		
		public static <K,V> KeyValue<K, V> of(Entry<K, V> value) {
			return of(value.getKey(),value.getValue());
		}
	}
}
