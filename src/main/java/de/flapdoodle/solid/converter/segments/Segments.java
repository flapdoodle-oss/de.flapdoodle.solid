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
package de.flapdoodle.solid.converter.segments;

import java.util.Optional;
import java.util.function.Function;

import com.google.common.annotations.VisibleForTesting;

public abstract class Segments {

	public static String map(String src, Function<String, String> defaultTransformation, Matcher...matchers) {
		StringBuilder sb=new StringBuilder();
		int lastEnd=0;
		boolean doloop=true;
		do {
			Optional<Replacement> optReplacement = firstOf(src, lastEnd, matchers);
			if (optReplacement.isPresent()) {
				Replacement replacement = optReplacement.get();
				if (lastEnd<replacement.start()) {
					sb.append(defaultTransformation.apply(src.substring(lastEnd, replacement.start())));
				}
				sb.append(replacement.content());
				lastEnd=replacement.end();
			} else {
				if (lastEnd<src.length()) {
					sb.append(defaultTransformation.apply(src.substring(lastEnd)));
				}
				doloop=false;
			}
		} while (doloop);
		
		return sb.toString();
	}
	
	@VisibleForTesting
	protected static Optional<Replacement> firstOf(String src, int start, Matcher ... matchers) {
		for (Matcher matcher : matchers) {
			Optional<Replacement> replacement = matcher.find(src, start);
			if (replacement.isPresent()) {
				return replacement;
			}
		}
		return Optional.empty();
	}

}
