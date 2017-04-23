package de.flapdoodle.solid.parser.meta;

import java.util.Map;

public class Yaml implements AsMap {

	private final Map<String, Object> map;

	public Yaml(Map<String, Object> map) {
		this.map = map;
	}

	@Override
	public Map<String, Object> asMap() {
		return map;
	}

	public static Yaml parse(String content) {
		return new Yaml((Map<String, Object>) new org.yaml.snakeyaml.Yaml().load(content));
	}
}
