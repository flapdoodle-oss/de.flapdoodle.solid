package de.flapdoodle.solid.theme.pebble;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TemplateLoaderTest {

	@Test
	public void resolvePath() {
		assertEquals("base/_page.html", TemplateLoader.resolvePath("base/_page", "home.html"));
		assertEquals("home.html", TemplateLoader.resolvePath("../home", "base/_page.html"));
		assertEquals("base/_footer.html", TemplateLoader.resolvePath("_footer", "base/_page.html"));
	}
}
