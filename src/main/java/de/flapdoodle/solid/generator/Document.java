package de.flapdoodle.solid.generator;

import org.immutables.value.Value.Immutable;

import com.google.common.collect.ImmutableList;

@Immutable
public interface Document {
	ImmutableList<String> path();
	
	Content content();
}
