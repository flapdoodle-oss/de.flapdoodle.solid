package de.flapdoodle.solid.generator;

import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.parser.content.Site;

public interface SiteGenerator {
	ImmutableList<Document> generate(Site site);
}
