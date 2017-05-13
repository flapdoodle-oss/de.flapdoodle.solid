package de.flapdoodle.solid.formatter;

import java.util.Optional;

public class DefaultObjectFormatter implements Formatter {

	@Override
	public Optional<String> format(Object value) {
		if (value instanceof String) {
			return Optional.of((String) value);
		}
		if (value instanceof Integer) {
			return Optional.of(value.toString());
		}
		return Optional.empty();
	}

}
