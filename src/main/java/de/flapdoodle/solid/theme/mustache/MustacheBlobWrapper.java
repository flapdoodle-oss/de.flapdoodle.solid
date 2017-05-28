package de.flapdoodle.solid.theme.mustache;

import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Lazy;
import org.immutables.value.Value.Parameter;

import de.flapdoodle.solid.content.render.MarkupRenderer;
import de.flapdoodle.solid.content.render.MarkupRendererFactory;
import de.flapdoodle.solid.content.render.RenderContext;
import de.flapdoodle.solid.parser.content.Blob;
import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.solid.types.tree.PropertyTree;

@Immutable
public abstract class MustacheBlobWrapper {
	@Parameter
	protected abstract Blob blob();
	@Parameter
	protected abstract MarkupRenderer markupRenderer();
	
	@Auxiliary
	public PropertyTree getMeta() {
		return blob().meta();
	}
	
	@Lazy
	public String asHtml() {
		return markupRenderer().asHtml(RenderContext.builder()
				.urlMapping(s -> Maybe.absent())
				.build(), blob().content());
	}
	
	
	public static MustacheBlobWrapper of(Blob src, MarkupRendererFactory factory) {
		return ImmutableMustacheBlobWrapper.of(src, factory.rendererFor(src.contentType()));
	}
}
