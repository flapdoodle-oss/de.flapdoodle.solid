package com.mitchellbosecke.pebble.attributes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;

import com.mitchellbosecke.pebble.error.PebbleException;

public class MethodResolverTest {

	@Test
	public void simpleGetter() throws PebbleException {
		Optional<ResolvedAttribute> resolved = new MethodResolver().resolve(new Mock(), "foo", new Object[0], false, "filename", 1);
		assertTrue(resolved.isPresent());
		assertEquals("foo", resolved.get().evaluate());
	}
	
	@Test
	public void oneArg() throws PebbleException {
		Optional<ResolvedAttribute> resolved = new MethodResolver().resolve(new Mock(), "foo", new Object[] { 1L }, false, "filename", 1);
		assertTrue(resolved.isPresent());
		assertEquals("foo 1", resolved.get().evaluate());
	}
	
	@Test
	public void directMethod() throws PebbleException {
		Optional<ResolvedAttribute> resolved = new MethodResolver().resolve(new Mock(), "getFoo", new Object[] { 1L }, false, "filename", 1);
		assertTrue(resolved.isPresent());
		assertEquals("foo 1", resolved.get().evaluate());
	}
	
	@Test
	public void varArg() throws PebbleException {
		Optional<ResolvedAttribute> resolved = new MethodResolver().resolve(new Mock(), "varArg", new Object[] { "one", "two" }, false, "filename", 1);
		assertTrue(resolved.isPresent());
		assertEquals("foo [one, two]", resolved.get().evaluate());
	}
	
	private static class Mock {
		public String getFoo() {
			return "foo";
		}
		
		public String getFoo(int arg) {
			return "foo "+arg;
		}
		
		public String getVarArg(String ...keys) {
			return "foo "+Arrays.asList(keys);
		}
	}
}
