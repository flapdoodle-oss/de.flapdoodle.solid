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
					.mapToUncheckedException(SomethingWentWrong::new)
					.get());
	}
}
