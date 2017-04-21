package de.flapdoodle.solid.types;

public class Types {

	public static <T> Maybe<T> isInstance(Class<T> type, Object value) {
		return type.isInstance(value) ? Maybe.of((T) value) : Maybe.nothing();
	}
}
