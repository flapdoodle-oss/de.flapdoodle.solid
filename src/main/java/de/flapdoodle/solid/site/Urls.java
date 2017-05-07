package de.flapdoodle.solid.site;

import java.util.Optional;

import org.immutables.value.Value.Immutable;

import com.google.common.collect.ImmutableMap;

@Immutable
public interface Urls {
	ImmutableMap<String, Config> configs();

	@Immutable
	interface Config {
		String path();
		Optional<Integer> itemsPerPage();
		
		public static ImmutableConfig.Builder builder() {
			return ImmutableConfig.builder();
		}
	}
	
	public static ImmutableUrls.Builder builder() {
		return ImmutableUrls.builder();
	}
}
