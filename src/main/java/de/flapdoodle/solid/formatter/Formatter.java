package de.flapdoodle.solid.formatter;

import de.flapdoodle.solid.types.Maybe;

public interface Formatter {
	Maybe<String> format(Object value);
}
