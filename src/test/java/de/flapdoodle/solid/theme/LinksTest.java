package de.flapdoodle.solid.theme;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.vavr.Tuple;

public class LinksTest {

	@Test
	public void stripDomainPart() {
		assertEquals(Tuple.of("http://foo.bar", "/"), Links.splitDomainPart("http://foo.bar/"));
		assertEquals(Tuple.of("http://foo.bar", "/bar"), Links.splitDomainPart("http://foo.bar/bar"));
		assertEquals(Tuple.of("http://foo.bar", "/baz/nix"), Links.splitDomainPart("http://foo.bar/baz/nix"));
		assertEquals(Tuple.of("http://foo.bar", "/"), Links.splitDomainPart("http://foo.bar"));
		assertEquals(Tuple.of("", "/blub"), Links.splitDomainPart("/blub"));
	}
}
