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
package de.flapdoodle.solid.parser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.flapdoodle.solid.exceptions.NotASolidSite;
import de.flapdoodle.solid.exceptions.SomethingWentWrong;
import de.flapdoodle.solid.io.Filenames;
import de.flapdoodle.solid.io.In;
import de.flapdoodle.solid.parser.content.Blob;
import de.flapdoodle.solid.parser.content.BlobParser;
import de.flapdoodle.solid.parser.content.ImmutableSite.Builder;
import de.flapdoodle.solid.parser.content.Site;
import de.flapdoodle.solid.parser.types.FiletypeParserFactory;
import de.flapdoodle.solid.parser.types.PropertyTreeParserFactory;
import de.flapdoodle.solid.site.SiteConfig;
import de.flapdoodle.types.Try;

public class DefaultSiteFactory implements SiteFactory {

	private final PropertyTreeParserFactory parserFactory;
	private final BlobParser blobParser;

	public DefaultSiteFactory(PropertyTreeParserFactory parserFactory, BlobParser blobParser) {
		this.parserFactory = parserFactory;
		this.blobParser = blobParser;
	}
	
	@Override
	public Site siteOf(Path siteRoot) {
		SiteConfig siteConfig = parse(siteRoot, parserFactory);
		return collect(siteRoot, siteConfig, blobParser);
	}
	
	private static Site collect(Path siteRoot, SiteConfig siteConfig, BlobParser blobParser) {
		Builder siteBuilder = Site.builder()
				.config(siteConfig);
		
		Try.runable(() ->	Files.walk(siteRoot.resolve(siteConfig.contentDirectory()))
				.forEach(path -> {
					if (path.toFile().isFile()) {
						Path relativePath = siteRoot.relativize(path);
						
						System.out.println(" -> "+relativePath);
						
						Optional<Blob> blob = Try.supplier(() -> blobParser.parse(relativePath, In.read(path)))
							.mapCheckedException(SomethingWentWrong::new)
							.get();
						if (blob.isPresent()) {
							siteBuilder.addBlobs(blob.get());
						} else {
							siteBuilder.addIgnoredFiles(relativePath.toString());
						}
					}}))
		.mapCheckedException(SomethingWentWrong::new)
		.run();
		
		return siteBuilder.build();
	}
	
	private static SiteConfig parse(Path siteRoot, PropertyTreeParserFactory parserFactory) {
		
		FiletypeParserFactory filetypeParserFactory=FiletypeParserFactory.defaults(parserFactory);
		
		Function<? super Path, ? extends Optional<SiteConfig>> path2Config = path -> 
			filetypeParserFactory.parserFor(Filenames.extensionOf(path))
				.map(p -> Try.supplier(() -> p.parse(In.read(path)))
						.mapCheckedException(SomethingWentWrong::new)
						.get())
				.map(config -> SiteConfig.of(Filenames.filenameOf(path), config));
		
		List<SiteConfig> configs = Try.supplier(() -> Files.list(siteRoot)
				.filter(p -> Filenames.filenameOf(p).startsWith("solid."))
				.map(path2Config)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList()))
			.mapCheckedException(SomethingWentWrong::new)
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
