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

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import de.flapdoodle.solid.exceptions.NotASolidSite;
import de.flapdoodle.solid.exceptions.SomethingWentWrong;
import de.flapdoodle.solid.io.Filenames;
import de.flapdoodle.solid.io.In;
import de.flapdoodle.solid.parser.content.Site;
import de.flapdoodle.solid.parser.types.FiletypeParserFactory;
import de.flapdoodle.solid.site.SiteConfig;
import de.flapdoodle.types.Try;

public class SiteParser {
	
	private final Path siteRoot;
	private final SiteConfig siteConfig;

	public SiteParser(Path siteRoot, SiteConfig siteConfig) {
		this.siteRoot = Preconditions.checkNotNull(siteRoot);
		this.siteConfig = Preconditions.checkNotNull(siteConfig);
	}
	
	public Site collect() {
		Try.runable(() ->	Files.walk(siteRoot.resolve(siteConfig.contentDirectory())).forEach(path -> {
			System.out.println(" -> "+path);
		}))
		.mapCheckedException(SomethingWentWrong::new)
		.run();
		return Site.builder()
				.config(siteConfig)
				
				.build();
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass())
				.addValue(siteConfig)
				.toString();
	}

	public static SiteParser parse(Path siteRoot) {
		FiletypeParserFactory filetypeParserFactory = FiletypeParserFactory.defaults();
		
		Function<? super Path, ? extends Optional<SiteConfig>> path2Config = path -> 
			filetypeParserFactory.parserFor(Filenames.extensionOf(path))
				.map(p -> Try.supplier(() -> p.parse(In.read(path)))
						.mapCheckedException(SomethingWentWrong::new)
						.get())
				.map(config -> SiteConfig.of(Filenames.filenameOf(path), config));
		
		List<SiteConfig> configs = Try.supplier(() -> Files.list(siteRoot)
				.map(path2Config)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList()))
			.mapCheckedException(SomethingWentWrong::new)
			.get();
		
		if (configs.size()!=1) {
			throw new NotASolidSite(siteRoot, filetypeParserFactory.supportedExtensions());
		}
		
		return new SiteParser(siteRoot, configs.get(0));
	}
}
