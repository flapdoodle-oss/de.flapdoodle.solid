package de.flapdoodle.solid.parser;

import java.nio.file.Path;

import de.flapdoodle.solid.parser.content.Site;

public interface SiteFactory {
	Site siteOf(Path siteRoot);
}
