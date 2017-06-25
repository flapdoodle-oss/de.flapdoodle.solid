package de.flapdoodle.solid.generator;

import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.types.tree.PropertyTree;

public interface PropertyCollectionResolver {
	ImmutableList<?> resolve(PropertyTree tree, Iterable<String> path);

}
