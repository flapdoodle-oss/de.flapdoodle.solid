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
package de.flapdoodle.solid.theme;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.flapdoodle.solid.parser.content.Blob;
import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.solid.types.maps.CountMap;

public abstract class LinkFactories {

	public interface Named {
		Maybe<Blobs> byId(String id);
	}

	public interface Blobs {
		Maybe<OneBlob> filterBy(Blob blob);

		Filtered filter();

		Menu asMenu();
	}

	public interface Menu {

		ImmutableList<MenuItem> items();
	}

	public interface MenuItem {
		String title();

		String getLink();

		ImmutableList<MenuItem> children();
	}

	public interface OneBlob {
		String getLink();

		String getLink(String key, Object value);
	}


	public interface Filtered {

		Filtered reversed();

		Filtered by(String key, Object value);

		Filtered firstPage();

		ImmutableSet<Object> values(String key);

		String getLink();

		int count();

		Filtered orderBy(String key);

		default ImmutableMap<Object, Integer> countMap(String key) {
			return values(key).stream()
				.collect(ImmutableMap.toImmutableMap(s -> s, s -> by(key,s).count()));
		}

		default ImmutableMap<Object, Integer> scaledCountMap(String key, int lowerBound, int upperBound) {
			return CountMap.scale(countMap(key), lowerBound, upperBound);
		}
	}
}
