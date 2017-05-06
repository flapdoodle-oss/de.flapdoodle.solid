package de.flapdoodle.solid.generator;

import org.immutables.value.Value.Parameter;

import de.flapdoodle.solid.types.ByteArray;

public interface Binary extends Content {
	@Override
	@Parameter
	String mimeType();
	
	@Parameter
	ByteArray data();
}
