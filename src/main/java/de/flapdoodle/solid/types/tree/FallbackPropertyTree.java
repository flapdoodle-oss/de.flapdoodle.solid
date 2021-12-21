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
package de.flapdoodle.solid.types.tree;

import java.util.List;
import java.util.Set;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import de.flapdoodle.types.Either;

public class FallbackPropertyTree implements PropertyTree {

	private final PropertyTree src;
	private final PropertyTree fallback;
	
	private final ImmutableSet<String> firstProperties;
	private final ImmutableSet<String> allProperties;

	public FallbackPropertyTree(PropertyTree src, PropertyTree fallback) {
		this.src = src;
		this.fallback = fallback;
		this.firstProperties = ImmutableSet.copyOf(src.properties());
		this.allProperties = ImmutableSet.copyOf(Sets.union(firstProperties, fallback.properties()));
	}

	@Override
	public Set<String> properties() {
		return allProperties;
	}

	@Override
	public List<Either<Object, ? extends PropertyTree>> get(String key) {
		if (firstProperties.contains(key)) {
			return src.get(key);
		}
		return fallback.get(key);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass())
				.add("src", src)
				.add("fallback", fallback)
				.toString();
	}
}
