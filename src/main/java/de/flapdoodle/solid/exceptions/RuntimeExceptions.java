package de.flapdoodle.solid.exceptions;

import java.util.function.Function;
import java.util.function.Supplier;

public class RuntimeExceptions {
	
	@Deprecated
	public static <T> Supplier<T> onException(Supplier<T> supplier, Function<RuntimeException, RuntimeException> exceptionFactory) {
		return () -> {
			try {
				return supplier.get();
			} catch (RuntimeException rx) {
				throw exceptionFactory.apply(rx);
			}
		};
	}
}
