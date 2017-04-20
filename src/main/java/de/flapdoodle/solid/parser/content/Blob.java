package de.flapdoodle.solid.parser.content;

import org.immutables.value.Value;

@Value.Immutable
public interface Blob {
	String contentType();
	String content();
}
