package de.flapdoodle.solid.converter.wordpress;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MetaMarkdownTest {

	@Test
	public void escapeYaml() {
		StringBuilder sb=new StringBuilder();
		MetaMarkdown.property(sb, "A", "Umfrage : welches Framework benutzt Du?");
		MetaMarkdown.property(sb, "B", "2009-09-09T07:16:41+00:00");
		String result = sb.toString();
		
		assertEquals("A: 'Umfrage : welches Framework benutzt Du?'\n" + 
				"B: 2009-09-09T07:16:41+00:00\n", result);
	}
}
