package de.flapdoodle.solid.sinks;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.flapdoodle.solid.types.Pair;

public class UndertowPageSinkTest {

	@Test
	public void hostAndPath() {
		Pair<String, String> hostAndBasePath = UndertowPageSink.hostAndBasePath("http://fooo.bar:1234/path");
		assertEquals("http://fooo.bar:1234",hostAndBasePath.a());
		assertEquals("/path/",hostAndBasePath.b());
	}
}
