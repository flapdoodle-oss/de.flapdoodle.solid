package de.flapdoodle.solid.theme.stringtemplate;

import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Lazy;

import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.content.render.MarkupRendererFactory;
import de.flapdoodle.solid.parser.content.Blob;
import de.flapdoodle.solid.theme.Context;
import de.flapdoodle.solid.theme.Paths;

@Immutable
public abstract class StringtemplateWrapper {
	protected abstract MarkupRendererFactory markupRenderFactory();
	protected abstract ImmutableList<Blob> allBlobs();
	public abstract Context context();
	
	@Auxiliary
	public StringtemplateBlobWrapper getSingle() {
		return allBlobs().size()==1 ? StringtemplateBlobWrapper.of(allBlobs().get(0),markupRenderFactory()) : null;
	}
	
	@Lazy
	public ImmutableList<StringtemplateBlobWrapper> getBlobs() {
		return allBlobs().stream()
				.map(b -> StringtemplateBlobWrapper.of(b, markupRenderFactory()))
				.collect(ImmutableList.toImmutableList());
	}
	
	@Lazy
	public StringtemplateSiteWrapper getSite() {
		return StringtemplateSiteWrapper.of(context().site().config());
	}
	
	@Auxiliary
	public String getUrl() {
		return context().paths().currentUrl();
	}

	@Auxiliary
	public Paths getPaths() {
		return context().paths();
	}

	public static ImmutableStringtemplateWrapper.Builder builder() {
		return ImmutableStringtemplateWrapper.builder();
	}
}