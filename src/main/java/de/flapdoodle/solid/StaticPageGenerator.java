package de.flapdoodle.solid;

import de.flapdoodle.solid.generator.SiteGenerator;

public interface StaticPageGenerator {
	Runnable generator(SiteSpring siteSpring, SiteGenerator generator, PageSink pageSink);
	
	public static StaticPageGenerator once() {
		return (siteSpring, generator, pageSink) -> () -> {
			pageSink.accept(generator.generate(siteSpring.get()));
		};
	}
}
