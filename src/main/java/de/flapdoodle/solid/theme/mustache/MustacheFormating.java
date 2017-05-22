package de.flapdoodle.solid.theme.mustache;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

@Immutable
interface MustacheFormating {
	@Parameter
	Object value();
	
	public static MustacheFormating of(Object value) {
		return ImmutableMustacheFormating.of(value);
	}
}