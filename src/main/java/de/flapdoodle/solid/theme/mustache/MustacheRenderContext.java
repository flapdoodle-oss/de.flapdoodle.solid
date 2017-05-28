package de.flapdoodle.solid.theme.mustache;

import org.immutables.value.Value.Immutable;

import de.flapdoodle.solid.content.render.MarkupRendererFactory;

@Immutable
public abstract class MustacheRenderContext {
	
	protected abstract MarkupRendererFactory markupRenderFactory();
	
	
	public static ImmutableMustacheRenderContext.Builder builder() {
		return ImmutableMustacheRenderContext.builder();
	}
}
