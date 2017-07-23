package de.flapdoodle.solid.converter.wordpress;

import org.immutables.value.Value.Immutable;

@Immutable
public interface WordpressRss {
	
	WpChannel channel();

	public static ImmutableWordpressRss.Builder builder() {
		return ImmutableWordpressRss.builder();
	}
}
