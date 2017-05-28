package de.flapdoodle.solid.content.render;

import java.util.function.Function;

import org.immutables.value.Value.Immutable;

import de.flapdoodle.solid.types.Maybe;

@Immutable
public interface RenderContext {
	Function<String, Maybe<String>> urlMapping();
	
	public static ImmutableRenderContext.Builder builder() {
		return ImmutableRenderContext.builder();
	}
}
