package de.flapdoodle.solid.types;

import org.junit.Test;

public class GroupedPropertyMapPrinterTest {
	
	@Test
	public void prettyPrintShouldIndentCorrect() {
		ImmutableGroupedPropertyMap map = GroupedPropertyMap.builder()
			.put("top", "bar")
			.put("gear", 17)
			.put("server", "port", "sub")
			.put("server", "path", "root", "sub-sub")
			.put("c", "sub-sub")
			.build();
		
		String pretty = GroupedPropertyMapPrinter.prettyPrinted(map);
		System.out.println(pretty);
	}
}
