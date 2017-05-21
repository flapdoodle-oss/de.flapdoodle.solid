package de.flapdoodle.solid.formatter;

import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.solid.types.tree.PropertyTree;

public class UrlFormatter implements Formatter {
	
	@Override
	public Maybe<String> format(Object value) {
		if (value instanceof String) {
			String src=(String) value;
			return Maybe.of(src.replace(' ', '-')
					.replace("--", "-")
					.toLowerCase()
					.replaceAll("[^a-zA-Z0-9/\\-_.]", ""));
		}
		return Maybe.absent();
	}

	public static Maybe<Formatter> parse(PropertyTree config) {
		if (config.find(String.class, "type").orElse("").equals("Url")) {
			return Maybe.of(new UrlFormatter());
		}
		return Maybe.absent();
	}

}
