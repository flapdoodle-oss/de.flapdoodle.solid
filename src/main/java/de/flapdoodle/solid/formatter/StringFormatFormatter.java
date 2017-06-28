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
package de.flapdoodle.solid.formatter;

import java.util.Collection;

import de.flapdoodle.solid.types.Maybe;

public class StringFormatFormatter implements Formatter {

	private final String formatString;

	public StringFormatFormatter(String formatString) {
		this.formatString = formatString;
	}
	
	@Override
	public Maybe<String> format(Object value) {
		if ((value instanceof Collection) && (((Collection) value).size()==1)) {
			return Maybe.of(String.format(formatString, ((Collection) value).iterator().next()));
		}
		return Maybe.of(String.format(formatString, value));
	}

}
