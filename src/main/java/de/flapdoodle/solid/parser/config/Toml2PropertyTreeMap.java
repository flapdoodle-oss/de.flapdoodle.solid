package de.flapdoodle.solid.parser.config;

import java.util.Map;

import com.moandjiezana.toml.Toml;

import de.flapdoodle.solid.types.ImmutablePropertyTreeMap;
import de.flapdoodle.solid.types.ImmutablePropertyTreeMap.MapBuilder;
import de.flapdoodle.solid.types.PropertyTreeMap;

public class Toml2PropertyTreeMap implements AsPropertyTreeMap<Toml> {

	@Override
	public PropertyTreeMap asPropertyTreeMap(Toml source) {
		MapBuilder builder = ImmutablePropertyTreeMap.builder();
		fill(builder, source.toMap());
		return builder.build();
	}

	private void fill(MapBuilder builder, Map<String, Object> map) {
		map.forEach((key, value) -> {
			if (value instanceof Map) {
				fill(builder.start(key),(Map<String, Object>) value);
			} else {
				builder.put(key, value);
			}
		});
	}

}
