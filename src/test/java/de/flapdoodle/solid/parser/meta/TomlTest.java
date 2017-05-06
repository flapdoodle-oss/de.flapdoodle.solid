package de.flapdoodle.solid.parser.meta;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import de.flapdoodle.solid.parser.types.Toml2GroupedPropertyMap;
import de.flapdoodle.solid.types.maps.GroupedPropertyMap;

public class TomlTest {

	@Test
	public void arraysOfTableSample() throws IOException {
		String source=Resources.toString(Resources.getResource(getClass(), "arraysOfTable.toml"), Charsets.UTF_8);
		Toml toml = Toml.parse(source);
		Map<String, Object> asMap = toml.asMap();
		System.out.println("map -> "+asMap);
		
		GroupedPropertyMap groupedMap = new Toml2GroupedPropertyMap().asGroupedPropertyMap(toml);
		System.out.println("map -> "+groupedMap.prettyPrinted());
	}
}
