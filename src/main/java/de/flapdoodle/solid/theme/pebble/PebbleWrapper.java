package de.flapdoodle.solid.theme.pebble;

import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Lazy;

import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.content.render.MarkupRendererFactory;
import de.flapdoodle.solid.parser.content.Blob;
import de.flapdoodle.solid.theme.Context;
import de.flapdoodle.solid.theme.Paths;

@Immutable
public abstract class PebbleWrapper {
	protected abstract MarkupRendererFactory markupRenderFactory();
	protected abstract ImmutableList<Blob> allBlobs();
	public abstract Context context();
	
	@Auxiliary
	public PebbleBlobWrapper getSingle() {
		return allBlobs().size()==1 ? PebbleBlobWrapper.of(allBlobs().get(0),markupRenderFactory()) : null;
	}
	
	@Lazy
	public ImmutableList<PebbleBlobWrapper> getBlobs() {
		return allBlobs().stream()
				.map(b -> PebbleBlobWrapper.of(b, markupRenderFactory()))
				.collect(ImmutableList.toImmutableList());
	}
	
	@Lazy
	public PebbleSiteWrapper getSite() {
		return PebbleSiteWrapper.of(context().site().config());
	}
	
	@Auxiliary
	public String getUrl() {
		return context().paths().currentUrl();
	}

	@Auxiliary
	public Paths getPaths() {
		return context().paths();
	}

	public static ImmutablePebbleWrapper.Builder builder() {
		return ImmutablePebbleWrapper.builder();
	}
}
