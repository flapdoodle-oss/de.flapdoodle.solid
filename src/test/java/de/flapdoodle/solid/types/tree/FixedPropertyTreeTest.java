package de.flapdoodle.solid.types.tree;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FixedPropertyTreeTest {

	@Test
	public void simpleMap() {
		PropertyTree tree = FixedPropertyTree.builder()
			.put("string", "Start")
			.put("number", 17)
			.build();
		
		assertEquals("[string, number]", tree.properties().toString());
		assertEquals("Start", tree.get("string").get(0).left());
		assertEquals(17, tree.get("number").iterator().next().left());
	}
	
	@Test
	public void subProperty() {
		PropertyTree tree = FixedPropertyTree.builder()
			.put("sub", FixedPropertyTree.builder()
					.build())
			.put("number", 17)
			.build();
		
		assertEquals("[sub, number]", tree.properties().toString());
		assertEquals("[]", tree.get("sub").get(0).right().properties().toString());
		assertEquals(17, tree.get("number").get(0).left());
	}
}
