package de.flapdoodle.solid.converter.wordpress;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.dom4j.DocumentException;
import org.junit.Test;

import com.google.common.io.Resources;

import de.flapdoodle.solid.xml.XmlParser;

public class WordpressRssConverterTest {

	@Test
	public void sampleWordpressDump() throws IOException, DocumentException {
		try (Reader reader = new InputStreamReader(Resources.asByteSource(Resources.getResource(getClass(), "wordpress-sample-rss.xml")).openBufferedStream())) {
			WordpressRss wordpressRss = WordpressRssConverter.build(XmlParser.of(reader));
			System.out.println(wordpressRss);
		}
	}
}
