package de.flapdoodle.solid.generator;

import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;

import de.flapdoodle.solid.parser.content.Blob;
import de.flapdoodle.solid.parser.path.Path;

@Immutable
public interface GroupedBlobs {
	
	Path currentPath();
	ImmutableMultimap<ImmutableMap<String, Object>, Blob> groupedBlobs();
	
	@Auxiliary
	default ImmutableCollection<ImmutableMap<String,Object>> keysOf(Blob blob) {
		return groupedBlobs().inverse().get(blob);
	}
	
	public static ImmutableGroupedBlobs.Builder builder() {
		return ImmutableGroupedBlobs.builder();
	}
}