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
package de.flapdoodle.solid.parser.regex;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.flapdoodle.types.Either;

public abstract class Patterns {

	private Patterns() {
		// no instance
	}
	
	public static void parse(Pattern pattern, String src, Consumer<Either<String, Matcher>> consumer) {
		Matcher matcher = pattern.matcher(src);
		int lastEnd=0;
		while (matcher.find()) {
			consumer.accept(Either.left(src.substring(lastEnd,matcher.start())));
			consumer.accept(Either.right(matcher));
			lastEnd=matcher.end();
		}
		consumer.accept(Either.left(src.substring(lastEnd)));
	}
}
