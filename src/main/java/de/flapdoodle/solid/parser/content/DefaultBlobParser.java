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
package de.flapdoodle.solid.parser.content;

import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.exceptions.RuntimeExceptions;
import de.flapdoodle.solid.io.Filenames;
import de.flapdoodle.solid.parser.meta.Toml;
import de.flapdoodle.solid.parser.meta.Yaml;
import de.flapdoodle.solid.parser.types.PropertyTreeParserFactory;
import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.solid.types.tree.FixedPropertyTree;
import de.flapdoodle.solid.types.tree.PropertyTree;

public class DefaultBlobParser implements BlobParser {

	private static final Pattern TOML_START = Pattern.compile("(?m)(?d)^\\+{3}");
	private static final Pattern TOML_END = TOML_START;
	private static final Pattern YAML_START = Pattern.compile("(?m)(?d)^\\-{3}");
	private static final Pattern YAML_END = YAML_START;
	
	private final PropertyTreeParserFactory parserFactory;
	
	public DefaultBlobParser(PropertyTreeParserFactory parserFactory) {
		this.parserFactory = parserFactory;
	}

	@Override
	public Maybe<Blob> parse(Path path, String content) {
		return RuntimeExceptions.onException(() -> interalParse(path, content), ex -> new RuntimeException("could not parse "+path,ex))
				.get();
	}

	private Maybe<Blob> interalParse(Path path, String content) {
		String filename = Filenames.filenameOf(path);
		String extension = Filenames.extensionOf(filename);
		Maybe<ContentType> contentType = ContentType.ofExtension(extension);
		if (contentType.isPresent()) {
			Maybe<ParsedMetaAndContent> optMetaAndContent = findToml(parserFactory, content);
			if (!optMetaAndContent.isPresent()) {
				optMetaAndContent = findYaml(parserFactory, content);
			}
			
			if (optMetaAndContent.isPresent()) {
				ParsedMetaAndContent metaAndContent = optMetaAndContent.get();
				PropertyTree meta = metaAndContent.meta();
				
				ImmutableList<String> blobPath = Filenames.pathAsList(path.getParent());
				
				PropertyTree blobMetaData = FixedPropertyTree.builder()
					.put("path", Joiner.on('/').join(blobPath)+"/")
					.put("filename", filename)
					.build()
					.overriding(meta);
				
				return Maybe.of(Blob.builder()
//					.addAllPath(blobPath)
//					.filename(filename)
					.meta(blobMetaData)
					.contentType(contentType.get())
					.content(metaAndContent.content())
					.build());
			}
		}
		
		return Maybe.empty();
	}

	@VisibleForTesting
	protected static Maybe<ParsedMetaAndContent> findToml(PropertyTreeParserFactory parserFactory, String content) {
		return findMeta(TOML_START, TOML_END, content)
				.map(mc -> ParsedMetaAndContent.of(parserFactory.parserFor(Toml.class).get().parse(mc.meta()), mc.content()));
	}

	@VisibleForTesting
	protected static Maybe<ParsedMetaAndContent> findYaml(PropertyTreeParserFactory parserFactory, String content) {
		return findMeta(YAML_START, YAML_END, content)
				.map(mc -> ParsedMetaAndContent.of(parserFactory.parserFor(Yaml.class).get().parse(mc.meta()), mc.content()));
	}
	
	@VisibleForTesting
	protected static Maybe<MetaAndContent> findMeta(Pattern startMarker, Pattern endMarker, String content) {
		Matcher startMatcher = startMarker.matcher(content);
		
		if (startMatcher.find()) {
			int startOfMeta = startMatcher.end();
			
			Matcher endMatcher = endMarker.matcher(content);
			if (endMatcher.find(startOfMeta)) {
				int endOfMeta=endMatcher.start();
				int startOfContent=endMatcher.end();
				return Maybe.of(MetaAndContent.of(content.substring(startOfMeta, endOfMeta), content.substring(startOfContent)));
			}
		}
		
		return Maybe.empty();
	}
	
	@Value.Immutable
	interface MetaAndContent {
		@Parameter
		String meta();
		@Parameter
		String content();
		
		public static MetaAndContent of(String meta, String content) {
			return ImmutableMetaAndContent.of(meta, content);
		}
	}
}
