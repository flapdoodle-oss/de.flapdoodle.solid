package de.flapdoodle.solid.site;

import org.immutables.value.Value;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

@Value.Immutable
public interface SiteConfig {
	
	String filename();
	
	String baseUrl();
	
	Optional<String> theme();
	
	ImmutableMap<String, String> properties();
	
	public static ImmutableSiteConfig.Builder builder() {
		return ImmutableSiteConfig.builder();
	}
}
