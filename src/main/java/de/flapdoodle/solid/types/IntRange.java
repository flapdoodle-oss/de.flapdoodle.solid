package de.flapdoodle.solid.types;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

@Immutable
public interface IntRange {
	@Parameter
	int start();
	@Parameter
	int end();
	
	public static IntRange of(int start, int end) {
		return ImmutableIntRange.of(start, end);
	}
}
