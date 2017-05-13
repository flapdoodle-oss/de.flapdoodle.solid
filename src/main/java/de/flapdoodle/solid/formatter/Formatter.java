package de.flapdoodle.solid.formatter;

import java.util.Optional;

public interface Formatter {
	Optional<String> format(Object value);
}
