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
package de.flapdoodle.solid.theme.pebble2;

import com.google.common.collect.ImmutableMap;
import de.flapdoodle.solid.types.Maybe;
import io.pebbletemplates.pebble.error.LoaderException;

import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

public class StringMapLoader extends AbstractLoader {

	private final ImmutableMap<String, String> templates;

	public StringMapLoader(Map<String, String> templates) {
		this.templates=ImmutableMap.copyOf(templates);
	}

	@Override
	public boolean resourceExists(String templateName) {
		return templates.containsKey(templateName);
	}

	@Override
	public Reader getReader(String cacheKey) throws LoaderException {
		Maybe<StringReader> reader = Maybe.ofNullable(templates.get(cacheKey))
				.map(StringReader::new);
		
		if (!reader.isPresent()) {
			throw new LoaderException(new RuntimeException("not in map"), "could not load "+cacheKey);
		}
		
		return reader.get();
	}

}
