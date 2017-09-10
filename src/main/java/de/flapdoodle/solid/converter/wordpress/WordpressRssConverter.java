/**
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.dom4j.DocumentException;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.flapdoodle.solid.converter.wordpress.ImmutableWordpressRss.Builder;
import de.flapdoodle.solid.generator.Binary;
import de.flapdoodle.solid.generator.Content;
import de.flapdoodle.solid.generator.Document;
import de.flapdoodle.solid.generator.Text;
import de.flapdoodle.solid.xml.Visitor;
import de.flapdoodle.solid.xml.XmlParser;
import de.flapdoodle.types.Try;

@SuppressWarnings("ucd")
public class WordpressRssConverter {

	public static void main(String[] args) throws IOException, DocumentException {
		System.out.println("solid wordpress converter");
		Preconditions.checkArgument(args.length>=2,"usage: <rss-xml> <exportDirectory>");
		
		Path rssXml = Paths.get(args[0]);
		Path target = Paths.get(args[1]);
		
		try (Reader reader = new InputStreamReader(Files.newInputStream(rssXml, StandardOpenOption.READ))) {
			WordpressRss wordpressRss = WordpressRssConverter.build(XmlParser.of(reader));
			ImmutableList<Document> documents = WordpressRss2Solid.convert(wordpressRss);
			ImmutableSet<String> allPaths = documents.stream()
				.map(Document::path)
				.collect(ImmutableSet.toImmutableSet());
			Preconditions.checkArgument(allPaths.size()==documents.size(),"path collisions");
			
			// create directory for page and post
			createDirectoryIfNotExist(target.resolve("post"));
			createDirectoryIfNotExist(target.resolve("page"));
			
			documents.forEach((Document d) -> {
				Path filePath = target.resolve(d.path());
				Try.runable(() -> {
					Files.write(filePath, asBytes(d.content()), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
				})
				.mapCheckedException(RuntimeException::new)
				.run();
			});
		}
	}

	private static void createDirectoryIfNotExist(Path dir) throws IOException {
		if (!dir.toFile().exists()) {
			Files.createDirectory(dir);
		}
	}
	
	private static byte[] asBytes(Content content) {
		if (content instanceof Text) {
			Text text = (Text) content;
			return text.text().getBytes(text.encoding());
		}
		if (content instanceof Binary) {
			return ((Binary) content).data().data();
		}
		throw new IllegalArgumentException("unknown content: "+content);
	}

	@VisibleForTesting
	protected static WordpressRss build(XmlParser xml) {
		return xml.collect(WordpressRssConverter::root);
	}
	
	private static WordpressRss root(Visitor visitor) {
		Builder builder = WordpressRss.builder();
		ImmutableList<WpChannel> channels = visitor.collect(WpChannel::channel);
		Preconditions.checkArgument(channels.size()==1,"more or less then one channel: %s",channels);
		builder.channel(channels.get(0));
		return builder.build();
	}

	public static LocalDateTime parseWpDate(String src) {
		return LocalDateTime.parse(src, WordpressRssConverter.DEFAULT_FORMAT);
	}

	public static final DateTimeFormatter DEFAULT_FORMAT=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	

}
