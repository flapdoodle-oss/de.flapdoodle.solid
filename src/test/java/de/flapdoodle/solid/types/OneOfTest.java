package de.flapdoodle.solid.types;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.flapdoodle.solid.types.OneOf.OneOfFactory;

public class OneOfTest {

	@Test
	public void simpleFactory() {
		OneOfFactory oneOf = OneOf.type(String.class);
		assertNotNull(oneOf);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void canNotCreateFactoryForDependendClasses() {
		OneOf.type(Number.class,String.class, Double.class);
	}
}
