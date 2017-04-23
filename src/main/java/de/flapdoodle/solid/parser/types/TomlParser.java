package de.flapdoodle.solid.parser.types;

import de.flapdoodle.solid.parser.meta.Toml;
import de.flapdoodle.solid.types.GroupedPropertyMap;

public class TomlParser implements Parser {

	private final AsGroupedPropertyMap<Toml> toml2GroupedPropertyMap;

	public TomlParser(AsGroupedPropertyMap<Toml> toml2GroupedPropertyMap) {
		this.toml2GroupedPropertyMap = toml2GroupedPropertyMap;
	}

	@Override
	public GroupedPropertyMap parse(String content) {
		return toml2GroupedPropertyMap.asGroupedPropertyMap(Toml.parse(content));
	}

}
