package de.flapdoodle.solid.generator;

import java.util.Optional;

import com.google.common.collect.ImmutableMap;

import de.flapdoodle.solid.formatter.Formatter;
import de.flapdoodle.solid.parser.path.Path;

public interface PathRenderer {
	Optional<String> render(Path path, ImmutableMap<String, Object> properties,FormatterOfProperty propertyFormatter);
	
	public static PathRenderer defaultPathRenderer() {
		return new DefaultPathRenderer();
	}
	
	interface FormatterOfProperty {
		Formatter of(String property,Optional<String> formatterName);
	}
}
