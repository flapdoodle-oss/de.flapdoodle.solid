package de.flapdoodle.solid.theme;

import org.immutables.value.Value.Immutable;

import com.google.common.collect.ImmutableMap;

import de.flapdoodle.solid.parser.content.Site;

@Immutable
public interface Context {
	Site site();
	ImmutableMap<String, Object> pathProperties();
	Paths paths();
	
	public static ImmutableContext.Builder builder() {
		return ImmutableContext.builder();
	}
}