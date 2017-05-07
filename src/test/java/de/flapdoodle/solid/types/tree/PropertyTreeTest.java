package de.flapdoodle.solid.types.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class PropertyTreeTest {

	@Test
	public void simpleProperty() {
		PropertyTree tree = FixedPropertyTree.builder()
				.put("simple", 17)
				.build();
		
		assertEquals(Integer.valueOf(17),tree.find(Integer.class, "simple").get());
	}

	@Test
	public void propertyInSub() {
		PropertyTree tree = FixedPropertyTree.builder()
				.put("sub", FixedPropertyTree.builder()
						.put("simple", 17)
						.build())
				.build();
		
		assertEquals(Integer.valueOf(17),tree.find(Integer.class, "sub", "simple").get());
	}
	
	@Test
	public void wontFindAnythingIfMoreThanOneInList() {
		PropertyTree tree = FixedPropertyTree.builder()
				.put("sub", FixedPropertyTree.builder()
						.put("simple", 17)
						.build())
				.put("sub", FixedPropertyTree.builder()
						.put("simple", 18)
						.build())
				.build();
		
		assertFalse(tree.find(Integer.class, "sub", "simple").isPresent());
	}
	
	@Test
	public void wontFindAnythingBecausePathEndsOnPropertyTree() {
		PropertyTree tree = FixedPropertyTree.builder()
				.put("sub", FixedPropertyTree.builder()
						.put("simple", 17)
						.build())
				.build();
		
		assertFalse(tree.find(Integer.class, "sub").isPresent());
	}
	
	@Test
	public void wontFindAnythingBecauseOfNoMatchingPropertyTree() {
		PropertyTree tree = FixedPropertyTree.builder()
				.put("sub", 19)
				.build();
		
		assertFalse(tree.find(Integer.class, "sub", "simple").isPresent());
	}
	
	@Test
	public void wontFindAnythingBecauseOfTypeMissmatch() {
		PropertyTree tree = FixedPropertyTree.builder()
				.put("simple", 19)
				.build();
		
		assertFalse(tree.find(String.class, "simple").isPresent());
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void emptyPathIsInvalid() {
		PropertyTree tree = FixedPropertyTree.builder()
				.put("simple", 19)
				.build();
		
		tree.find(String.class);
	}
}
