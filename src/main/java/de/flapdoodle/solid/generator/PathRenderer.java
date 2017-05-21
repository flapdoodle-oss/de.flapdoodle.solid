package de.flapdoodle.solid.generator;

import com.google.common.collect.ImmutableMap;

import de.flapdoodle.solid.formatter.Formatter;
import de.flapdoodle.solid.parser.path.Path;
import de.flapdoodle.solid.types.Maybe;

public interface PathRenderer {
	Maybe<String> render(Path path, ImmutableMap<String, Object> properties,FormatterOfProperty propertyFormatter);
	
	public static PathRenderer defaultPathRenderer() {
		return new DefaultPathRenderer();
	}
	
	interface FormatterOfProperty {
		Formatter of(String property,Maybe<String> formatterName);
	}
}
