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

public interface Matcher {
	Optional<Replacement> find(String src, int currentPosition);
	
	public static Matcher of(String start, String end) {
		return of(start, end, s -> s);
	}
	
	public static Matcher of(String start, String end, Function<String, String> map) {
		return (src, pos) -> {
			int idxA=src.indexOf(start, pos);
			if (idxA!=-1) {
				int idxE = src.indexOf(end, idxA+start.length());
				if (idxE!=-1) {
					return Optional.of(Replacement.of(idxA, idxE+end.length(), map.apply(src.substring(idxA, idxE+end.length()))));
				}
			}
			return Optional.empty();
		};
	}
}
