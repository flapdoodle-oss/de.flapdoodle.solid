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
package de.flapdoodle.solid.types;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

public class OneOf {

	public static OneOfFactory type(Class<?> ...classes) {
		return new OneOfFactory(ImmutableSet.copyOf(classes));
	}
	
	public static class OneOfFactory {

		private final ImmutableSet<Class<?>> classes;

		public OneOfFactory(ImmutableSet<Class<?>> classes) {
			Iterables.everyCombinationOf(classes, (left,right) -> {
				Preconditions.checkArgument(!left.isAssignableFrom(right) && !right.isAssignableFrom(left),"class %s is parent/child of %s",left, right);
			});
			this.classes = classes;
		}

	}
}
