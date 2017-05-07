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

import java.util.Set;
import java.util.function.BiConsumer;

import com.google.common.collect.ImmutableList;

public abstract class Iterables {

	private Iterables() {
		// no instance
	}
	
	public static <T> ImmutableList<Pair<T,T>> everyCombinationOf(Set<T> set) {
		ImmutableList.Builder<Pair<T,T>> builder=ImmutableList.builder();
		everyCombinationOf(set, (left, right) -> {
			builder.add(Pair.of(left, right));
		});
		return builder.build();
	}
	
	public static <T> void everyCombinationOf(Set<T> set, BiConsumer<T, T> consumer) {
		ImmutableList<T> asList = ImmutableList.copyOf(set);
		for (int l=0;(l+1)<asList.size();l++) {
			T left=asList.get(l);
			for (int r=l+1;r<asList.size();r++) {
				T right=asList.get(r);
				consumer.accept(left, right);
			}
		}
	}
}
