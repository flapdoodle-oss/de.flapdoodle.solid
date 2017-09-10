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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.solid.types.properties.TypeProperties;
import de.flapdoodle.solid.types.properties.TypePropertiesLookup;
import de.flapdoodle.solid.types.properties.TypeProperty;
import de.flapdoodle.solid.types.tree.PropertyTree;
import de.flapdoodle.types.Either;

public class TypePropertyBasePropertyCollectionResolver implements PropertyCollectionResolver {
	
	private final TypePropertiesLookup lookup;

	public TypePropertyBasePropertyCollectionResolver(TypePropertiesLookup lookup) {
		this.lookup = lookup;
	}
	

	@Override
	public ImmutableSet<?> resolve(PropertyTree tree, Iterable<String> path) {
		ImmutableList<String> pathAsList = ImmutableList.copyOf(path);
		return resolveInternal(tree, pathAsList);
	}


	private ImmutableSet<?> resolveInternal(PropertyTree tree, ImmutableList<String> pathAsList) {
		if (!pathAsList.isEmpty()) {
			String currentPropertName = pathAsList.get(0);
			ImmutableList<String> leftPath = pathAsList.subList(1, pathAsList.size());
			ImmutableList<Either<Object, ? extends PropertyTree>> result = tree.findList(e -> Maybe.of(e), currentPropertName);
			long valueCount = result.stream().filter(e -> e.isLeft()).count();
			if (valueCount == result.size()) {
				return result.stream()
					.map((Either<Object, ? extends PropertyTree> e) -> e.left())
					.map((Object o) -> resolve(o, leftPath.iterator(), lookup))
					.filter(Maybe::isPresent)
					.map(Maybe::get)
					.collect(ImmutableSet.toImmutableSet());
			} else {
				if (valueCount==0) {
					return result.stream()
						.map(e -> e.right())
						.flatMap(p -> resolveInternal(p, leftPath).stream())
						.collect(ImmutableSet.toImmutableSet());
				}
			}
		}
		return ImmutableSet.of();
	}

	private static <T> Maybe<?> resolve(T instance, Iterator<? extends String> iterator, TypePropertiesLookup lookup) {
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
