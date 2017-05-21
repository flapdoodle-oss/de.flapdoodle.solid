package de.flapdoodle.solid.formatter;

import de.flapdoodle.solid.types.Maybe;

public class DefaultObjectFormatter implements Formatter {

	@Override
	public Maybe<String> format(Object value) {
		if (value instanceof String) {
			return Maybe.of((String) value);
		}
		if (value instanceof Integer) {
			return Maybe.of(value.toString());
		}
		return Maybe.absent();
	}

}
