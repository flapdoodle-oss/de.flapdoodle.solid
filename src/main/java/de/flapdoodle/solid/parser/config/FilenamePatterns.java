package de.flapdoodle.solid.parser.config;

import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

import com.google.common.collect.ImmutableSet;

import de.flapdoodle.solid.parser.config.ImmutableFilenamePatterns.Builder;

@Value.Immutable
public interface FilenamePatterns {
	
	@Parameter
	ImmutableSet<String> patterns();
	
	public static FilenamePatterns of(String... patterns) {
		return builder()
				.addPatterns(patterns)
				.build();
	}

	public static Builder builder() {
		return ImmutableFilenamePatterns.builder();
	}
	
}
