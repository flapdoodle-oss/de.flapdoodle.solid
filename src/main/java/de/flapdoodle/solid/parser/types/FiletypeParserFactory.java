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
package de.flapdoodle.solid.parser.types;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.flapdoodle.solid.parser.meta.Toml;
import de.flapdoodle.solid.parser.meta.Yaml;
import de.flapdoodle.solid.types.Maybe;

public interface FiletypeParserFactory {
	
	Maybe<PropertyTreeParser> parserFor(String extension);
	
	ImmutableSet<String> supportedExtensions();
	
	public static FiletypeParserFactory defaults(PropertyTreeParserFactory parserFactory) {
		ImmutableMap<String, Class<?>> parser=ImmutableMap.<String,Class<?>>builder()
				.put("toml", Toml.class)
				.put("yaml", Yaml.class)
				.build();
		
		return new FiletypeParserFactory() {
			@Override
			public Maybe<PropertyTreeParser> parserFor(String extension) {
				return Maybe.ofNullable(parser.get(extension))
						.flatMap(parserFactory::parserFor);
			}
			
			@Override
			public ImmutableSet<String> supportedExtensions() {
				return parser.keySet();
			}
		};
	}
}
