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
package de.flapdoodle.solid.types.tree;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

import de.flapdoodle.types.Either;

//wie json

//key = value -> [value]
//key = [ ... ]
//key = { ... } -> [{}]
//value -> String | Number | Date
//key = [{}]|[value]

//map = key -> [ value | map ]
public interface PropertyTree {
	Set<String> properties();
	List<Either<Object, ? extends PropertyTree>> get(String key);
	
	default String prettyPrinted() {
		return PropertyTreePrinter.prettyPrinted(this);
	}
	
	default Optional<PropertyTree> find(String ...path) {
		return find(propertyTree(), FluentIterable.from(path));
	}
	
	default ImmutableList<PropertyTree> findList(String ...path) {
		return findList(propertyTree(), FluentIterable.from(path));
	}
	
	default <T> Optional<T> find(Class<T> type, String ... path) {
		return find(matchingType(type), FluentIterable.from(path));
	}
	
	default <T> Optional<T> find(Class<T> type, Iterable<String> path) {
		return find(matchingType(type), path);
	}

	default <T> ImmutableList<T> findList(Class<T> type, String ... path) {
		return findList(matchingType(type), FluentIterable.from(path));
	}
	
	default <T> ImmutableList<T> findList(Class<T> type, Iterable<String> path) {
		return findList(matchingType(type), path);
	}

	default <T> Optional<T> find(Function<Either<Object, ? extends PropertyTree>,Optional<T>> map, String ... path) {
		return find(map, FluentIterable.from(path));
	}
	
	default <T> Optional<T> find(Function<Either<Object, ? extends PropertyTree>,Optional<T>> map, Iterable<String> path) {
		ImmutableList<T> result = findList(map, path);
		return result.size()==1 
				? Optional.of(result.get(0)) 
				: Optional.empty();
	}

	default <T> ImmutableList<T> findList(Function<Either<Object, ? extends PropertyTree>,Optional<T>> map, String ... path) {
		return findList(map, FluentIterable.from(path));
	}
	
	default <T> ImmutableList<T> findList(Function<Either<Object, ? extends PropertyTree>,Optional<T>> map, Iterable<String> path) {
		Iterator<String> iterator = path.iterator();
		Preconditions.checkArgument(iterator.hasNext(),"empty path: %s",path);
		Optional<PropertyTree> current = Optional.of(this);
		
		while (current.isPresent() && iterator.hasNext()) {
			String key=iterator.next();
			List<Either<Object, ? extends PropertyTree>> list = current.get().get(key);
			
			if (!iterator.hasNext()) {
				ImmutableList<T> result = list.stream().map(map).filter(Optional::isPresent).map(Optional::get).collect(ImmutableList.toImmutableList());
				if (result.size()==list.size()) {
					return result;
				} else {
					return ImmutableList.of();
				}
			} else {
				if (list.size()==1) {
					Either<Object, ? extends PropertyTree> value = list.get(0);
					if (!value.isLeft()) {
						current=Optional.of(value.right());
					} else {
						return ImmutableList.of();
					}
				} else {
					return ImmutableList.of();
				}
			}
		}
		
		return ImmutableList.of();
	}
	
	public static <T> Function<Either<Object, ? extends PropertyTree>, Optional<T>> matchingType(Class<T> type) {
		return either -> either.isLeft() && type.isInstance(either.left()) 
				? Optional.of((T) either.left()) 
				: Optional.empty();
	}
	
	public static Function<Either<Object, ? extends PropertyTree>, Optional<PropertyTree>> propertyTree() {
		return either -> !either.isLeft() 
				? Optional.of(either.right()) 
				: Optional.empty();
	}
}
