package de.flapdoodle.solid.parser.content;

import org.immutables.value.Value;

import de.flapdoodle.solid.site.SiteConfig;

@Value.Immutable
public interface Site {
	SiteConfig config();
	
	public static ImmutableSite.Builder builder() {
		return ImmutableSite.builder();
	}
}
