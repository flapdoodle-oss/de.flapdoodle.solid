package de.flapdoodle.solid.theme.mustache;

import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Lazy;

import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.parser.content.Blob;
import de.flapdoodle.solid.theme.Context;
import de.flapdoodle.solid.theme.Paths;

@Immutable
interface MustacheWrapper {
	ImmutableList<Blob> blobs();
	Context context();
	
	@Auxiliary
	default Blob getSingle() {
		return blobs().size()==1 ? blobs().get(0) : null;
	}
	
	@Lazy
	default MustacheSiteWrapper getSite() {
		return MustacheSiteWrapper.of(context().site().config());
	}
	
	@Auxiliary
	default String getUrl() {
		return context().paths().currentUrl();
	}

	@Auxiliary
	default Paths getPaths() {
		return context().paths();
	}

	public static ImmutableMustacheWrapper.Builder builder() {
		return ImmutableMustacheWrapper.builder();
	}
}