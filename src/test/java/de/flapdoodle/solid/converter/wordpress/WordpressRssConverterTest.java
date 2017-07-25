package de.flapdoodle.solid.converter.wordpress;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.dom4j.DocumentException;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;

import de.flapdoodle.solid.generator.Document;
import de.flapdoodle.solid.xml.XmlParser;

public class WordpressRssConverterTest {

	@Test
	public void sampleWordpressDump() throws IOException, DocumentException {
		try (Reader reader = new InputStreamReader(Resources.asByteSource(Resources.getResource(getClass(), "wordpress-sample-rss.xml")).openBufferedStream())) {
			WordpressRss wordpressRss = WordpressRssConverter.build(XmlParser.of(reader));
			System.out.println("------------------------");
			System.out.println(wordpressRss);
			System.out.println("------------------------");
			ImmutableList<Document> documents = WordpressRss2Solid.convert(wordpressRss);
			documents.forEach(doc -> {
				String docAsString = doc.toString();
				int idx = docAsString.indexOf("---");
				if (idx!=-1) {
					int idxE = docAsString.indexOf("---", idx+3);
					if (idxE!=-1) {
						docAsString=docAsString.substring(0, idxE+3);
					}
				}
				System.out.println(docAsString);
			});
			System.out.println("------------------------");
		}
	}
}
