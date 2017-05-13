package de.flapdoodle.solid.generator;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import de.flapdoodle.solid.formatter.DefaultObjectFormatter;
import de.flapdoodle.solid.generator.PathRenderer.FormatterOfProperty;
import de.flapdoodle.solid.parser.path.Path;

public class DefaultPathRendererTest {

	@Test
	public void pagePathShouldOmitFirstPage() {
		DefaultPathRenderer renderer = new DefaultPathRenderer();
		FormatterOfProperty formatter=(property, formatterName) -> new DefaultObjectFormatter();
		Optional<String> result = renderer.render(Path.parse("foo/:bar/:page/"), ImmutableMap.of("bar","baz","page",1), formatter);
		assertEquals("foo/baz/",result.get());
	}
}
