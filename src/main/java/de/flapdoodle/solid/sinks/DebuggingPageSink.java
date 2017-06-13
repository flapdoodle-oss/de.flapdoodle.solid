package de.flapdoodle.solid.sinks;

import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.PageSink;
import de.flapdoodle.solid.generator.Document;
import de.flapdoodle.solid.generator.Text;
import de.flapdoodle.solid.site.SiteConfig;

public final class DebuggingPageSink implements PageSink {
	
	private final boolean showContent;

	public DebuggingPageSink(boolean showContent) {
		this.showContent = showContent;
	}
	
	@Override
	public void accept(SiteConfig siteConfig, ImmutableList<Document> documents) {
		if (!documents.isEmpty()) {
			System.out.println("-------------------------");
			System.out.println("Documents: ");
			documents.forEach(d -> {
				System.out.println(" - "+d.path());
			});
			System.out.println("-------------------------");
			if (showContent) {
				documents.forEach(d -> {
				if (d.content() instanceof Text) {
					System.out.println(" - "+d.path());
					System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
					System.out.println(((Text) d.content()).text());
					System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
				}
			});
			}

		} else {
			System.out.println("-------------------------");
			System.out.println("No generated Documents.");
			System.out.println("-------------------------");
		}
	}
}