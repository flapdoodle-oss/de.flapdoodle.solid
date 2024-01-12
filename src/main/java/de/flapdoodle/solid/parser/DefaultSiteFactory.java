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
package de.flapdoodle.solid.parser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.exceptions.NotASolidSite;
import de.flapdoodle.solid.exceptions.SomethingWentWrong;
import de.flapdoodle.solid.generator.Document;
import de.flapdoodle.solid.io.Filenames;
import de.flapdoodle.solid.io.In;
import de.flapdoodle.solid.parser.content.Blob;
import de.flapdoodle.solid.parser.content.BlobParser;
import de.flapdoodle.solid.parser.content.ImmutableSite.Builder;
import de.flapdoodle.solid.parser.content.Site;
import de.flapdoodle.solid.parser.content.SiteConfigPostProcessor;
import de.flapdoodle.solid.parser.types.FiletypeParserFactory;
import de.flapdoodle.solid.parser.types.PropertyTreeParserFactory;
import de.flapdoodle.solid.site.SiteConfig;
import de.flapdoodle.solid.theme.Theme;
import de.flapdoodle.solid.theme.ThemeFactory;
import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.types.Try;

public class DefaultSiteFactory implements SiteFactory {

	private final PropertyTreeParserFactory parserFactory;
	private final BlobParser blobParser;
	private final ThemeFactory themeFactory;

	public DefaultSiteFactory(PropertyTreeParserFactory parserFactory, BlobParser blobParser, ThemeFactory themeFactory) {
		this.parserFactory = parserFactory;
		this.blobParser = blobParser;
		this.themeFactory = themeFactory;
	}

	@Override
	public Site siteOf(Path siteRoot) {
		SiteConfig siteConfig = parse(siteRoot, parserFactory);
		Theme theme = themeFactory.of(siteRoot.resolve("themes").resolve(siteConfig.theme()));
		return collect(siteRoot, siteConfig, theme, blobParser);
	}

	private static Site collect(Path siteRoot, SiteConfig siteConfig, Theme theme, BlobParser blobParser) {
		Builder siteBuilder = Site.builder()
				.config(siteConfig)
				.theme(theme);

		SiteConfigPostProcessor postProcessor = SiteConfigPostProcessor.of(siteConfig.postProcessing());

		Try.runable(() ->	{
			Path contentRoot = siteRoot.resolve(siteConfig.contentDirectory());
			In.walk(contentRoot, (relativePath,content) -> {
					Maybe<Blob> blob = blobParser.parse(contentRoot.relativize(relativePath), new String(content.data(), Charsets.UTF_8));
					if (blob.isPresent()) {
						siteBuilder.addBlobs(postProcessor.process(siteConfig, blob.get()));
					} else {
						siteBuilder.addIgnoredFiles(relativePath.toString());
					}
				});
		})
			.mapToUncheckedException(SomethingWentWrong::new)
			.run();

		siteBuilder.addAllStaticFiles(Try.supplier(() -> {
			Path staticContentPath = siteRoot.resolve(siteConfig.staticDirectory());
			return Document.of(staticContentPath, path -> path.toString());
		})
			.fallbackTo(ex -> ImmutableList.of())
			.get());

		return siteBuilder.build();
	}

	private static SiteConfig parse(Path siteRoot, PropertyTreeParserFactory parserFactory) {

		FiletypeParserFactory filetypeParserFactory=FiletypeParserFactory.defaults(parserFactory);

		Function<? super Path, ? extends Maybe<SiteConfig>> path2Config = path ->
			PropertyTreeConfigs.propertyTreeOf(filetypeParserFactory, path)
				.map(config -> SiteConfig.of(Filenames.filenameOf(path), config));

//		In.walk(siteRoot, (path,content) -> {
//			if (Filenames.filenameOf(path).startsWith("solid.")) {
//				return Maybe.of(value)
//			}
//		});

		List<SiteConfig> configs = Try.supplier(() -> Files.list(siteRoot)
				.filter(p -> Filenames.filenameOf(p).startsWith("solid."))
				.map(path2Config)
				.flatMap(Maybe::asStream)
				.collect(Collectors.toList()))
			.mapToUncheckedException(SomethingWentWrong::new)
			.get();

		if (configs.size()!=1) {
			throw new NotASolidSite(siteRoot, filetypeParserFactory.supportedExtensions()
					.stream()
					.map(s -> "solid."+s)
					.collect(Collectors.toList()));
		}

		return configs.get(0);
	}


}
