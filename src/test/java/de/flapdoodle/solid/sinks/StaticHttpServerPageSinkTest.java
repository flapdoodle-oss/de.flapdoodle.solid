package de.flapdoodle.solid.sinks;

import static org.junit.Assert.assertEquals;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class StaticHttpServerPageSinkTest {

	@Test
	public void resolvePathForUrl() {
		Path resolved = StaticHttpServerPageSink.resolve(Paths.get("foo", "bar"), "/this/is/a/path/");
		assertEquals("foo/bar/this/is/a/path/index.html",resolved.toString());
	}
}
