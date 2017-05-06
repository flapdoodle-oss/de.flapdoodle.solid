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
