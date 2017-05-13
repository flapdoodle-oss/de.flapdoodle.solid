package de.flapdoodle.solid.formatter;

import java.util.Optional;

import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.solid.types.tree.PropertyTree;

public class UrlFormatter implements Formatter {
	
	@Override
	public Optional<String> format(Object value) {
		if (value instanceof String) {
			String src=(String) value;
			return Optional.of(src.replace(' ', '-')
					.replace("--", "-")
					.toLowerCase()
					.replaceAll("[^a-zA-Z0-9/\\-_.]", ""));
		}
		return Optional.empty();
	}

	public static Maybe<Formatter> parse(PropertyTree config) {
		if (config.find(String.class, "type").orElse("").equals("Url")) {
			return Maybe.of(new UrlFormatter());
		}
		return Maybe.absent();
	}

}
