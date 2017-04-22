package de.flapdoodle.solid.parser.content;

import java.util.Optional;

public enum ContentType {
	Markdown, Html, Text;
	
	public static Optional<ContentType> ofExtension(String ext) {
		switch (ext) {
			case "md":
				return Optional.of(Markdown);
			case "html":
			case "htm":
				return Optional.of(Html);
			case "txt":
				return Optional.of(Text);
		}
		return Optional.empty();
	}
}
