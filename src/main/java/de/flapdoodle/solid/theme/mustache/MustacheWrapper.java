package de.flapdoodle.solid.theme.mustache;

import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Lazy;

import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.parser.content.Blob;
import de.flapdoodle.solid.theme.Context;
import de.flapdoodle.solid.theme.Paths;

@Immutable
abstract class MustacheWrapper {
	protected abstract MustacheRenderContext renderContext();
	protected abstract ImmutableList<Blob> allBlobs();
	public abstract Context context();
	
	@Auxiliary
	public MustacheBlobWrapper getSingle() {
		return allBlobs().size()==1 ? MustacheBlobWrapper.of(allBlobs().get(0), renderContext().markupRenderFactory()) : null;
	}
	
	@Auxiliary
	public ImmutableList<MustacheBlobWrapper> getBlobs() {
		return allBlobs().stream()
				.map(blob -> MustacheBlobWrapper.of(blob, renderContext().markupRenderFactory()))
				.collect(ImmutableList.toImmutableList());
	}
	
	@Lazy
	public MustacheSiteWrapper getSite() {
		return MustacheSiteWrapper.of(context().site().config());
	}
	
	@Auxiliary
	public String getUrl() {
		return context().paths().currentUrl();
	}

	@Auxiliary
	public Paths getPaths() {
		return context().paths();
	}

	public static ImmutableMustacheWrapper.Builder builder() {
		return ImmutableMustacheWrapper.builder();
	}
}