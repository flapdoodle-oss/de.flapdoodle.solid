package de.flapdoodle.solid.parser.types;

import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.moandjiezana.toml.Toml;

public interface FiletypeParserFactory {
	
	Optional<Parser> parserFor(String extension);
	
	ImmutableSet<String> supportedExtensions();
	
	public static FiletypeParserFactory defaults(ParserFactory parserFactory) {
		ImmutableMap<String, Class<?>> parser=ImmutableMap.<String,Class<?>>builder()
				.put("toml", Toml.class)
				.build();
		
		return new FiletypeParserFactory() {
			@Override
			public Optional<Parser> parserFor(String extension) {
				return Optional.ofNullable(parser.get(extension))
						.flatMap(parserFactory::parserFor);
			}
			
			@Override
			public ImmutableSet<String> supportedExtensions() {
				return parser.keySet();
			}
		};
	}
}
