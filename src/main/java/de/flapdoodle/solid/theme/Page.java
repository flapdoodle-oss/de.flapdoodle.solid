package de.flapdoodle.solid.theme;

import java.util.Optional;

import org.immutables.value.Value.Immutable;

@Immutable
public interface Page {
	String getUrl();
	Optional<String> getTitle();
	
	public static ImmutablePage.Builder builder() {
		return ImmutablePage.builder();
	}
}
