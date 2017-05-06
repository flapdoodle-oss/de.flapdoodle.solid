package de.flapdoodle.solid.types;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

@Immutable
public interface Pair<A,B> {
	@Parameter
	A a();
	@Parameter
	B b();
	
	public static <A,B> Pair<A,B> of(A a, B b) {
		return ImmutablePair.of(a, b);
	}
}
