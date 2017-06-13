package de.flapdoodle.solid.theme.pebble;

import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Lazy;
import org.immutables.value.Value.Parameter;

import de.flapdoodle.solid.content.render.MarkupRenderer;
import de.flapdoodle.solid.content.render.MarkupRendererFactory;
import de.flapdoodle.solid.content.render.RenderContext;
import de.flapdoodle.solid.parser.content.Blob;
import de.flapdoodle.solid.types.Maybe;

@Immutable
public abstract class PebbleBlobWrapper {
	@Parameter
	protected abstract Blob blob();
	@Parameter
	protected abstract MarkupRenderer markupRenderer();
	
	@Auxiliary
	public PebblePropertyTreeWrapper getMeta() {
		return PebblePropertyTreeWrapper.of(blob().meta());
	}
	
	@Lazy
	public String getAsHtml() {
		return markupRenderer().asHtml(RenderContext.builder()
				.urlMapping(s -> Maybe.absent())
				.build(), blob().content());
	}
	
	@Lazy
	public String getHtml(int incrementHeading) {
		return markupRenderer().asHtml(RenderContext.builder()
				.urlMapping(s -> Maybe.absent())
				.incrementHeading(incrementHeading)
				.build(), blob().content());
	}
	
	
	public static PebbleBlobWrapper of(Blob src, MarkupRendererFactory factory) {
		return ImmutablePebbleBlobWrapper.of(src, factory.rendererFor(src.contentType()));
	}
}
