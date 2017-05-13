package de.flapdoodle.solid.parser.content;

import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

import de.flapdoodle.solid.types.tree.PropertyTree;

@Value.Immutable
public interface ParsedMetaAndContent {
	@Parameter
	PropertyTree meta();
	@Parameter
	String content();
	
	public static ParsedMetaAndContent of(PropertyTree meta, String content) {
		return ImmutableParsedMetaAndContent.of(meta, content);
	}
}