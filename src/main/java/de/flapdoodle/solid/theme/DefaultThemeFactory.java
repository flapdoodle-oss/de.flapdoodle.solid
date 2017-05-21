package de.flapdoodle.solid.theme;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.exceptions.NotASolidSite;
import de.flapdoodle.solid.io.Filenames;
import de.flapdoodle.solid.parser.PropertyTreeConfigs;
import de.flapdoodle.solid.parser.types.FiletypeParserFactory;
import de.flapdoodle.solid.theme.mustache.MustacheTheme;
import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.solid.types.tree.PropertyTree;
import de.flapdoodle.types.Try;

public class DefaultThemeFactory implements ThemeFactory {

	private final FiletypeParserFactory filetypeParserFactory;

	public DefaultThemeFactory(FiletypeParserFactory filetypeParserFactory) {
		this.filetypeParserFactory = filetypeParserFactory;
	}

	@Override
	public Theme of(Path themeDirectory) {
		Function<Path, Maybe<PropertyTree>> path2Config = path -> PropertyTreeConfigs.propertyTreeOf(filetypeParserFactory, path);

		ImmutableList<PropertyTree> configs = Try.supplier(() -> Files.list(themeDirectory)
				.filter(p -> Filenames.filenameOf(p).startsWith("theme."))
				.map(path2Config)
				.flatMap(Maybe::asStream)
				.collect(ImmutableList.toImmutableList()))
				.mapCheckedException(RuntimeException::new)
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
			return new MustacheTheme(themeDirectory, config);
		}

		throw new RuntimeException("theme engine not supported: "+config.prettyPrinted());
	}

}
