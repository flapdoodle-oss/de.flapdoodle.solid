package de.flapdoodle.solid.parser.types;

import de.flapdoodle.solid.parser.meta.Yaml;
import de.flapdoodle.solid.types.GroupedPropertyMap;

public class YamlParser implements Parser {

	private final AsGroupedPropertyMap<Yaml> toml2GroupedPropertyMap;

	public YamlParser(AsGroupedPropertyMap<Yaml> toml2GroupedPropertyMap) {
		this.toml2GroupedPropertyMap = toml2GroupedPropertyMap;
	}

	@Override
	public GroupedPropertyMap parse(String content) {
		return toml2GroupedPropertyMap.asGroupedPropertyMap(Yaml.parse(content));
	}

}
