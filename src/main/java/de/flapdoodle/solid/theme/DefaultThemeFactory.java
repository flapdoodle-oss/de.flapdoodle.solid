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
package de.flapdoodle.solid.theme;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.content.render.MarkupRendererFactory;
import de.flapdoodle.solid.exceptions.NotASolidSite;
import de.flapdoodle.solid.io.Filenames;
import de.flapdoodle.solid.parser.PropertyTreeConfigs;
import de.flapdoodle.solid.parser.types.FiletypeParserFactory;
import de.flapdoodle.solid.theme.mustache.MustacheTheme;
import de.flapdoodle.solid.theme.pebble.PebbleTheme;
import de.flapdoodle.solid.theme.stringtemplate.StringtemplateTheme;
import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.solid.types.tree.PropertyTree;
import de.flapdoodle.types.Try;

public class DefaultThemeFactory implements ThemeFactory {

	private final FiletypeParserFactory filetypeParserFactory;
	private final MarkupRendererFactory markupRendererFactory;

	public DefaultThemeFactory(FiletypeParserFactory filetypeParserFactory, MarkupRendererFactory markupRendererFactory) {
		this.filetypeParserFactory = filetypeParserFactory;
		this.markupRendererFactory = markupRendererFactory;
	}

	@Override
	public Theme of(Path themeDirectory) {
		Function<Path, Maybe<PropertyTree>> path2Config = path -> PropertyTreeConfigs.propertyTreeOf(filetypeParserFactory, path);

		ImmutableList<PropertyTree> configs = Try.supplier(() -> Files.list(themeDirectory)
				.filter(p -> Filenames.filenameOf(p).startsWith("theme."))
				.map(path2Config)
				.flatMap(Maybe::asStream)
				.collect(ImmutableList.toImmutableList()))
				.mapToUncheckedException(RuntimeException::new)
				.get();

		if (configs.size() != 1) {
			throw new NotASolidSite(themeDirectory, filetypeParserFactory.supportedExtensions()
					.stream()
					.map(s -> "theme." + s)
					.collect(Collectors.toList()));
		}
		
		PropertyTree config = configs.get(0);
		Maybe<String> engine = config.find(String.class, "engine");
		if (engine.isPresent() && engine.get().equals("mustache")) {
			return new MustacheTheme(themeDirectory, config, markupRendererFactory);
		}
		if (engine.isPresent() && engine.get().equals("st")) {
			return new StringtemplateTheme(themeDirectory, config, markupRendererFactory);
		}
		if (engine.isPresent() && engine.get().equals("pebble")) {
			return new PebbleTheme(themeDirectory, config, markupRendererFactory);
		}

		throw new RuntimeException("theme engine not supported: "+config.prettyPrinted());
	}

}
