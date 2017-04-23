package de.flapdoodle.solid.parser.types;

import java.util.Optional;

import com.google.common.collect.ImmutableMap;

import de.flapdoodle.solid.parser.meta.Toml;

public interface ParserFactory {
	Optional<Parser> parserFor(Class<?> type);
	
	public static ParserFactory defaultFactory() {
		ImmutableMap<Class<?>, Parser> parser=ImmutableMap.<Class<?>,Parser>builder()
				.put(Toml.class, new TomlParser(new Toml2GroupedPropertyMap()))
				.build();
		
		return type -> Optional.ofNullable(parser.get(type));
	}
}
