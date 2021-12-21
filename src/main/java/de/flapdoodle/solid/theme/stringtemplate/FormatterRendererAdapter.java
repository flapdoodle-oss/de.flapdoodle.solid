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
package de.flapdoodle.solid.theme.stringtemplate;

import java.util.Locale;
import java.util.function.Function;

import de.flapdoodle.solid.formatter.Formatter;
import de.flapdoodle.solid.types.Maybe;

public class FormatterRendererAdapter<T> implements TypeRenderer<T> {

	private final Function<String, Maybe<Formatter>> formatterOfName;
	private final TypeRenderer<T> fallback;

	private FormatterRendererAdapter(Function<String, Maybe<Formatter>> formatterOfName, TypeRenderer<T> fallback) {
		this.formatterOfName = formatterOfName;
		this.fallback = fallback;
	}
	
	@Override
	public String render(T o, String formatString, Locale locale) {
		return formatterOfName.apply(formatString)
			.flatMap(f -> f.format(o))
			.orElse(() -> fallback.render(o, formatString, locale));
	}

	public static <T> FormatterRendererAdapter<T> of(Function<String, Maybe<Formatter>> formatterOfName, TypeRenderer<T> fallback) {
		return new FormatterRendererAdapter<>(formatterOfName, fallback);
	}
}
