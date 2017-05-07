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
package de.flapdoodle.solid.parser.types;

import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.parser.meta.Yaml;
import de.flapdoodle.solid.types.tree.FixedPropertyTree;
import de.flapdoodle.solid.types.tree.FixedPropertyTree.Builder;
import de.flapdoodle.solid.types.tree.PropertyTree;
import de.flapdoodle.types.Either;

public class Yaml2PropertyTree implements AsPropertyTree<Yaml> {

	@Override
	public PropertyTree asPropertyTree(Yaml source) {
		Builder builder = FixedPropertyTree.builder();
		fill(builder, source.asMap());
		return builder.build();
	}

	private static void fill(Builder builder, Map<String, Object> map) {
		map.forEach((key, value) -> {
			builder.putAllMap(key, map(value));
		});
	}

	private static ImmutableList<Either<Object, ? extends PropertyTree>> map(Object value) {
		if (value instanceof Map) {
			Builder builder = FixedPropertyTree.builder();
			fill(builder, (Map<String, Object>) value);
			return ImmutableList.of(Either.right(builder.build()));
		} else if (value instanceof Iterable) {
			Iterable iterable=(Iterable) value;
			ImmutableList.Builder<Either<Object, ? extends PropertyTree>> builder=ImmutableList.builder();
			for (Object v : iterable) {
				ImmutableList<Either<Object, ? extends PropertyTree>> mapped = map(v);
				Preconditions.checkArgument(mapped.size()==1,"more or less than one mapped value: %s",mapped);
				builder.add(mapped.get(0));
			}
			return builder.build();
		}
		return ImmutableList.of(Either.left(value));
	}
}
