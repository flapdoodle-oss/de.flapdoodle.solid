package de.flapdoodle.solid.parser;

import static org.junit.Assert.assertNotNull;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

public class SiteParserTest {

	@Test
	public void siteParserWillCheckForSolidConfigFirst() {
		Path siteARoot = Paths.get("src", "test","resources","sample","site-a");
		SiteParser parser = SiteParser.parse(siteARoot);
		assertNotNull(parser);
		System.out.println(" -> "+parser);
	}
}
