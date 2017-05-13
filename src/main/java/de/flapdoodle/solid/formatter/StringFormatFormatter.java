package de.flapdoodle.solid.formatter;

import java.util.Optional;

public class StringFormatFormatter implements Formatter {

	private final String formatString;

	public StringFormatFormatter(String formatString) {
		this.formatString = formatString;
	}
	
	@Override
	public Optional<String> format(Object value) {
		return Optional.of(String.format(formatString, value));
	}

}
