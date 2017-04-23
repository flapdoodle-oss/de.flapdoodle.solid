package de.flapdoodle.solid.parser.meta;

import java.util.Map;

public class Toml implements AsMap {
	private final Map<String, Object> map;

	public Toml(Map<String, Object> map) {
		this.map = map;
	}
	
	@Override
	public Map<String, Object> asMap() {
		return map;
	}

	public static Toml parse(String content) {
		return new Toml(new com.moandjiezana.toml.Toml().read(content).toMap());
	}
}
