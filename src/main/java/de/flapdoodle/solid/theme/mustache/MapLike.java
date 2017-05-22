package de.flapdoodle.solid.theme.mustache;

import de.flapdoodle.solid.types.Maybe;

public interface MapLike {
	Maybe<Object> get(String key);
}
