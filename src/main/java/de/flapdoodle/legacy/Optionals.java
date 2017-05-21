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
package de.flapdoodle.legacy;

import java.util.Optional;

import com.google.common.base.Preconditions;

import de.flapdoodle.solid.types.Maybe;

@Deprecated
public class Optionals {

	@Deprecated
	// move to java8/Preconditions
	public static <T> Optional<T> checkPresent(Optional<T> value, String message, Object ...args) {
		Preconditions.checkArgument(value.isPresent(),message,args);
		return value;
	}
	
	public static <T> Maybe<T> checkPresent(Maybe<T> value, String message, Object ...args) {
		Preconditions.checkArgument(value.isPresent(),message,args);
		return value;
	}
	
}
