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
package de.flapdoodle.solid.generator;

import java.util.Iterator;

import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.solid.types.properties.TypeProperties;
import de.flapdoodle.solid.types.properties.TypePropertiesLookup;
import de.flapdoodle.solid.types.properties.TypeProperty;
import de.flapdoodle.solid.types.tree.PropertyTree;
import de.flapdoodle.types.Either;

public class TypePropertyBasePropertyResolver implements PropertyResolver {

	private final TypePropertiesLookup lookup;

	public TypePropertyBasePropertyResolver(TypePropertiesLookup lookup) {
		this.lookup = lookup;
	}
	
	@Override
	public Maybe<?> resolve(PropertyTree tree, Iterable<String> path) {
		PropertyTree current=tree;
		Iterator<String> iterator = path.iterator();
		while (iterator.hasNext()) {
			String part = iterator.next();
			
			Maybe<Either<Object, ? extends PropertyTree>> found = current.find(e -> Maybe.of(e), part);
//			ImmutableList<Either<Object, ? extends PropertyTree>> foundList = current.findList(e -> Maybe.of(e), part);
			
			if (found.isPresent()) {
				Either<Object, ? extends PropertyTree> either = found.get();
				if (either.isLeft()) {
					return resolve(either.left(),iterator, lookup);
				} else {
					current=either.right();
				}
			} else {
				break;
			}
		}
		return Maybe.empty();
	}

	private static <T> Maybe<?> resolve(T instance, Iterator<String> iterator, TypePropertiesLookup lookup) {
		if (!iterator.hasNext()) {
			return Maybe.of(instance);
		}
		
		Maybe<TypeProperties<T>> properties = lookup.propertiesOf((Class<T>) instance.getClass());
		if (properties.isPresent()) {
			TypeProperties<T> typeProperties = properties.get();
			Maybe<TypeProperty<T, ?>> propertyOf = typeProperties.of(iterator.next());
			if (propertyOf.isPresent()) {
				Object newInstance = propertyOf.get().propertyOf(instance);
				return resolve(newInstance, iterator, lookup);
			}
		}
		return Maybe.empty();
	}
}
