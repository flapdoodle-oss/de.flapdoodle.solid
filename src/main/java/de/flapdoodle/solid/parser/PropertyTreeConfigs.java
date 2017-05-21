package de.flapdoodle.solid.parser;

import java.nio.file.Path;

import de.flapdoodle.solid.exceptions.SomethingWentWrong;
import de.flapdoodle.solid.io.Filenames;
import de.flapdoodle.solid.io.In;
import de.flapdoodle.solid.parser.types.FiletypeParserFactory;
import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.solid.types.tree.PropertyTree;
import de.flapdoodle.types.Try;

public abstract class PropertyTreeConfigs {

	public static Maybe<PropertyTree> propertyTreeOf(FiletypeParserFactory filetypeParserFactory, Path path) {
		 return filetypeParserFactory.parserFor(Filenames.extensionOf(path))
			.map(p -> Try.supplier(() -> p.parse(In.read(path)))
					.mapCheckedException(SomethingWentWrong::new)
					.get());
	}
}
