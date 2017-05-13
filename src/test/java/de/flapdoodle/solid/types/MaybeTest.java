package de.flapdoodle.solid.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;

public class MaybeTest {

	@Test
	public void some() {
		Maybe<String> maybe = Maybe.of("foo");
		
		assertEquals("foo",maybe.get());
		assertTrue(maybe.isPresent());
	}
	
	@Test
	public void optionals() {
		Optional<String> asOptional = Maybe.fromOptional(Optional.<String>empty()).asOptional();
		assertFalse(asOptional.isPresent());
		
		asOptional = Maybe.fromOptional(Optional.of("foo")).asOptional();
		assertTrue(asOptional.isPresent());
		assertEquals("foo",asOptional.get());
	}
}
