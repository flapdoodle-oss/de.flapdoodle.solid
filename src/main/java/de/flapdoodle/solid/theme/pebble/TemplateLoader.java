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
package de.flapdoodle.solid.theme.pebble;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Path;

import com.mitchellbosecke.pebble.error.LoaderException;

public class TemplateLoader extends AbstractLoader {

	private final Path templateRoot;

	public TemplateLoader(Path templateRoot) {
		this.templateRoot = templateRoot;
	}
	
	@Override
	public Reader getReader(String cacheKey) throws LoaderException {
		try {
			return new FileReader(templateRoot.resolve(cacheKey).toFile());
		}
		catch (FileNotFoundException e) {
			throw new LoaderException(e, "could not load "+cacheKey);
		}
	}

}
