package de.flapdoodle.solid.parser.config;

import de.flapdoodle.solid.types.PropertyTreeMap;

public interface AsPropertyTreeMap<T> {
	PropertyTreeMap asPropertyTreeMap(T source);
}
