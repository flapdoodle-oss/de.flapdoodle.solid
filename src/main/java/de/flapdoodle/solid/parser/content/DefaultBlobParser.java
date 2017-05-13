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
package de.flapdoodle.solid.parser.content;

import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

import com.google.common.annotations.VisibleForTesting;

import de.flapdoodle.solid.io.Filenames;
import de.flapdoodle.solid.parser.meta.Toml;
import de.flapdoodle.solid.parser.meta.Yaml;
import de.flapdoodle.solid.parser.types.PropertyTreeParserFactory;

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
	public Optional<Blob> parse(Path path, String content) {
		String filename = Filenames.filenameOf(path);
		String extension = Filenames.extensionOf(filename);
		Optional<ContentType> contentType = ContentType.ofExtension(extension);
		if (contentType.isPresent()) {
			Optional<ParsedMetaAndContent> metaAndContent = findToml(parserFactory, content);
			if (!metaAndContent.isPresent()) {
				metaAndContent = findYaml(parserFactory, content);
			}
			
			if (metaAndContent.isPresent()) {
				return Optional.of(Blob.builder()
					.addAllPath(Filenames.pathAsList(path.getParent()))
					.filename(filename)
					.meta(metaAndContent.get().meta())
					.contentType(contentType.get())
					.content(metaAndContent.get().content())
					.build());
			}
		}
		
		return Optional.empty();
	}

	@VisibleForTesting
	protected static Optional<ParsedMetaAndContent> findToml(PropertyTreeParserFactory parserFactory, String content) {
		return findMeta(TOML_START, TOML_END, content)
				.map(mc -> ParsedMetaAndContent.of(parserFactory.parserFor(Toml.class).get().parse(mc.meta()), mc.content()));
	}

	@VisibleForTesting
	protected static Optional<ParsedMetaAndContent> findYaml(PropertyTreeParserFactory parserFactory, String content) {
		return findMeta(YAML_START, YAML_END, content)
				.map(mc -> ParsedMetaAndContent.of(parserFactory.parserFor(Yaml.class).get().parse(mc.meta()), mc.content()));
	}
	
	@VisibleForTesting
	protected static Optional<MetaAndContent> findMeta(Pattern startMarker, Pattern endMarker, String content) {
		Matcher startMatcher = startMarker.matcher(content);
		
		if (startMatcher.find()) {
			int startOfMeta = startMatcher.end();
			
			Matcher endMatcher = endMarker.matcher(content);
			if (endMatcher.find(startOfMeta)) {
				int endOfMeta=endMatcher.start();
				int startOfContent=endMatcher.end();
				return Optional.of(MetaAndContent.of(content.substring(startOfMeta, endOfMeta), content.substring(startOfContent)));
			}
		}
		
		return Optional.empty();
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
