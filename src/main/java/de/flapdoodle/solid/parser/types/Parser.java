package de.flapdoodle.solid.parser.types;

import de.flapdoodle.solid.types.GroupedPropertyMap;

public interface Parser {
	GroupedPropertyMap parse(String content);
}
