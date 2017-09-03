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
