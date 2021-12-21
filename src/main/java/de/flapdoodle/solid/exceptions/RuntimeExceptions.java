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
