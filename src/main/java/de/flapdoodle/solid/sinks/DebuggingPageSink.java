package de.flapdoodle.solid.sinks;

import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.PageSink;
import de.flapdoodle.solid.generator.Document;

public final class DebuggingPageSink implements PageSink {
	@Override
	public void accept(ImmutableList<Document> documents) {
		if (!documents.isEmpty()) {
			System.out.println("-------------------------");
			System.out.println("Documents: ");
			documents.forEach(d -> {
				System.out.println(" - "+d.path());
			});
			System.out.println("-------------------------");
//			documents.forEach(d -> {
//				if (d.content() instanceof Text) {
//					System.out.println(" - "+d.path());
//					System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//					System.out.println(((Text) d.content()).text());
//					System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//				}
//			});

		} else {
			System.out.println("-------------------------");
			System.out.println("No generated Documents.");
			System.out.println("-------------------------");
		}
	}
}