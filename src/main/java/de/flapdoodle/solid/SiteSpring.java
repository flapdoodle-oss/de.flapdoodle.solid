package de.flapdoodle.solid;

import java.nio.file.Path;

import com.google.common.base.Supplier;

import de.flapdoodle.solid.parser.DefaultSiteFactory;
import de.flapdoodle.solid.parser.content.BlobParser;
import de.flapdoodle.solid.parser.content.DefaultBlobParser;
import de.flapdoodle.solid.parser.content.Site;
import de.flapdoodle.solid.parser.types.PropertyTreeParserFactory;

public interface SiteSpring extends Supplier<Site> {

	public static SiteSpring withPath(Path siteRoot) {
		return () -> {
			PropertyTreeParserFactory parserFactory = PropertyTreeParserFactory.defaultFactory();
			BlobParser blobParser = new DefaultBlobParser(parserFactory);
			DefaultSiteFactory siteFactory = new DefaultSiteFactory(parserFactory, blobParser);
			return siteFactory.siteOf(siteRoot);
		};
	}
}
