package de.flapdoodle.solid.theme.pebble;

import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.mitchellbosecke.pebble.error.LoaderException;

import de.flapdoodle.solid.types.Maybe;

public class StringMapLoader extends AbstractLoader {

	private final ImmutableMap<String, String> templates;

	public StringMapLoader(Map<String, String> templates) {
		this.templates=ImmutableMap.copyOf(templates);
	}
	
	@Override
	public Reader getReader(String cacheKey) throws LoaderException {
		Maybe<StringReader> reader = Maybe.ofNullable(templates.get(cacheKey))
				.map(StringReader::new);
		
		if (!reader.isPresent()) {
			throw new LoaderException(new RuntimeException("not in map"), "could not load "+cacheKey);
		}
		
		return reader.get();
	}

}
