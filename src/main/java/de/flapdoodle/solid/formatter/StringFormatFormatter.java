package de.flapdoodle.solid.formatter;

import de.flapdoodle.solid.types.Maybe;

public class StringFormatFormatter implements Formatter {

	private final String formatString;

	public StringFormatFormatter(String formatString) {
		this.formatString = formatString;
	}
	
	@Override
	public Maybe<String> format(Object value) {
		return Maybe.of(String.format(formatString, value));
	}

}
