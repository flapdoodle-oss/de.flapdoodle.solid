/*
 * Copyright (C) 2017
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
