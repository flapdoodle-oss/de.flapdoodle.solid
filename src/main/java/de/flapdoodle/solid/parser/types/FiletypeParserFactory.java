package de.flapdoodle.solid.parser.types;

import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public interface FiletypeParserFactory {
	
	Optional<Parser> parserFor(String extension);
	
	ImmutableSet<String> supportedExtensions();
	
	public static FiletypeParserFactory defaults() {
		ImmutableMap<String, Parser> parser=ImmutableMap.<String,Parser>builder()
				.put("toml", new TomlParser(new Toml2GroupedPropertyMap()))
				.build();
		
		return new FiletypeParserFactory() {
			@Override
			public Optional<Parser> parserFor(String extension) {
				return Optional.ofNullable(parser.get(extension));
			}
			
			@Override
			public ImmutableSet<String> supportedExtensions() {
				return parser.keySet();
			}
		};
	}
}
