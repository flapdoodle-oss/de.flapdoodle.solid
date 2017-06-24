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
package de.flapdoodle.solid.parser.content;

import com.google.common.base.Preconditions;

import de.flapdoodle.solid.formatter.DefaultObjectFormatter;
import de.flapdoodle.solid.generator.PathRenderer.FormatterOfProperty;
import de.flapdoodle.solid.types.Maybe;

public abstract class Sites {
	private Sites() {
		// no instance
	}

	public static FormatterOfProperty formatterOfProperty(Site site) {
		DefaultObjectFormatter defaultFormatter=new DefaultObjectFormatter();
		return (name,formatterName) -> {
			if (formatterName.isPresent()) {
				return Preconditions.checkNotNull(site.config().formatters().formatters().get(formatterName.get()),"could not get formatter %s",formatterName.get());
			}
			Maybe<String> defaultFormatterName = Maybe.ofNullable(site.config().defaultFormatter().get(name));
			if (defaultFormatterName.isPresent()) {
				return Preconditions.checkNotNull(site.config().formatters().formatters().get(defaultFormatterName.get()),"could not get formatter %s",defaultFormatterName.get());
			}
			return defaultFormatter;
		};
	}
}
