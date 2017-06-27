package de.flapdoodle.solid.generator;

import com.google.common.collect.ImmutableSet;

import de.flapdoodle.solid.types.properties.TypePropertiesLookup;
import de.flapdoodle.solid.types.tree.PropertyTree;

public interface PropertyCollectionResolver {
	ImmutableSet<?> resolve(PropertyTree tree, Iterable<String> path);

	static PropertyCollectionResolver defaultResolver() {
		return new TypePropertyBasePropertyCollectionResolver(TypePropertiesLookup.defaultLookup());
	}

}
