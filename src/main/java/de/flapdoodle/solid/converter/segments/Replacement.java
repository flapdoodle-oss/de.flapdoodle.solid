package de.flapdoodle.solid.converter.segments;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

@Immutable
public interface Replacement {
	@Parameter
	int start();
	@Parameter
	int end();
	@Parameter
	String content();
	
	public static Replacement of(int start,int end, String content) {
		return ImmutableReplacement.of(start, end, content);
	}
	
}
