package de.flapdoodle.solid.generator;

import java.nio.charset.Charset;

import org.immutables.value.Value.Default;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import com.google.common.base.Charsets;

@Immutable
public interface Text extends Content {
	@Override
	@Parameter
	String mimeType();
	
	@Parameter
	String text();
	
	@Default
	default Charset encoding() {
		return Charsets.UTF_8;
	}
}
