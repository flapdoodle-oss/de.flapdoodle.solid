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
package de.flapdoodle.solid.generator;

import com.google.common.collect.ImmutableMap;

import de.flapdoodle.solid.parser.path.Path;
import de.flapdoodle.solid.parser.path.Path.Property;
import de.flapdoodle.solid.types.Maybe;

public class DefaultPathRenderer implements PathRenderer {

	private final String baseUrl;

	public DefaultPathRenderer(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	@Override
	public Maybe<String> render(Path path, ImmutableMap<String, Object> properties, FormatterOfProperty propertyFormatter) {
		if (path.propertyNames().size()<=properties.keySet().size()) {
			StringBuilder sb=new StringBuilder();
			sb.append(baseUrl);
			
			for (Path.Part part : path.parts()) {
				if (part instanceof Path.Static) {
					sb.append(urlify(((Path.Static) part).fixed()));
				} else {
					if (part instanceof Path.Property) {
						Property property = (Path.Property) part;
						Maybe<Object> mappedValue = Maybe.ofNullable(properties.get(property.property()));
						Maybe<String> urlPart = mappedValue.flatMap(v -> propertyFormatter.of(property.property(), property.formatter()).format(v));
						if (urlPart.isPresent()) {
							String renderedPart = urlPart.get();
							
							if (property.property().equals(Path.PAGE)) {
								if (renderedPart.equals("1")) {
									break;
								}
								renderedPart=path.pathPrefix().orElse("")+renderedPart;
							}
							sb.append(urlify(renderedPart));
						} else {
							return Maybe.empty();
						}
					}
				}
			};
			return Maybe.of(sb.toString());
		}
		return Maybe.empty();
	}
	
	private static String urlify(String src) {
		return src.replace(' ', '-')
				.replace("--", "-")
				.toLowerCase()
				.replaceAll("[^a-zA-Z0-9/\\-_.]", "");
	}
}
