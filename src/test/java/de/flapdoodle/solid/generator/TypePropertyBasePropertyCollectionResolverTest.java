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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.solid.types.properties.TypeProperties;
import de.flapdoodle.solid.types.properties.TypePropertiesLookup;
import de.flapdoodle.solid.types.tree.FixedPropertyTree;
import de.flapdoodle.solid.types.tree.PropertyTree;

public class TypePropertyBasePropertyCollectionResolverTest {
	@Test
	public void unknownProperty() {
		TypePropertyBasePropertyCollectionResolver resolver = resolver(lookup(Void.class, TypeProperties.builder(Void.class).build()));

		PropertyTree tree=FixedPropertyTree.builder()
				.put("foo", "bar")
				.build();
		ImmutableSet<?> resolved = resolver.resolve(tree, ImmutableList.of("no"));
		assertTrue(resolved.isEmpty());
	}

	@Test
	public void unknownSubProperty() {
		TypePropertyBasePropertyCollectionResolver resolver = resolver(lookup(Void.class, TypeProperties.builder(Void.class).build()));

		PropertyTree tree=FixedPropertyTree.builder()
				.put("foo", "bar")
				.build();
		ImmutableSet<?> resolved = resolver.resolve(tree, ImmutableList.of("foo", "no"));
		assertTrue(resolved.isEmpty());
	}

	@Test
	public void simpleProperty() {
		TypePropertyBasePropertyCollectionResolver resolver = resolver(lookup(Void.class, TypeProperties.builder(Void.class).build()));

		PropertyTree tree=FixedPropertyTree.builder()
				.put("foo", "bar")
				.build();
		ImmutableSet<?> resolved = resolver.resolve(tree, ImmutableList.of("foo"));
		assertFalse(resolved.isEmpty());
		assertEquals(ImmutableSet.of("bar"), resolved);
	}

	@Test
	public void secondLevelProperty() {
		TypePropertyBasePropertyCollectionResolver resolver = resolver(lookup(Void.class, TypeProperties.builder(Void.class).build()));

		PropertyTree tree=FixedPropertyTree.builder()
				.put("foo", FixedPropertyTree.builder()
						.put("bar", "blob")
						.build())
				.build();
		ImmutableSet<?> resolved = resolver.resolve(tree, ImmutableList.of("foo","bar"));
		assertFalse(resolved.isEmpty());
		assertEquals(ImmutableSet.of("blob"), resolved);
	}

	@Test
	public void secondLevelAndTypeProperty() {
		TypePropertyBasePropertyCollectionResolver resolver = resolver(lookup(String.class, TypeProperties.builder(String.class)
				.putMap("len", s -> s.length())
				.build()));

		PropertyTree tree=FixedPropertyTree.builder()
				.put("foo", FixedPropertyTree.builder()
						.put("bar", "blob")
						.build())
				.build();
		ImmutableSet<?> resolved = resolver.resolve(tree, ImmutableList.of("foo","bar","len"));
		assertFalse(resolved.isEmpty());
		assertEquals(ImmutableSet.of(4), resolved);
	}

	@Test
	public void multipleSecondLevelAndTypeProperty() {
		TypePropertyBasePropertyCollectionResolver resolver = resolver(lookup(String.class, TypeProperties.builder(String.class)
				.putMap("len", s -> s.length())
				.build()));

		PropertyTree tree=FixedPropertyTree.builder()
				.put("foo", FixedPropertyTree.builder()
						.put("bar", "a")
						.put("bar", "bb")
						.put("bar", "ccc")
						.build())
				.build();
		ImmutableSet<?> resolved = resolver.resolve(tree, ImmutableList.of("foo","bar","len"));
		assertFalse(resolved.isEmpty());
		assertEquals(ImmutableSet.of(1,2,3), resolved);
	}
	@Test
	public void secondLevelAndUnkownTypeProperty() {
		TypePropertyBasePropertyCollectionResolver resolver = resolver(lookup(String.class, TypeProperties.builder(String.class)
				.putMap("len", s -> s.length())
				.build()));

		PropertyTree tree=FixedPropertyTree.builder()
				.put("foo", FixedPropertyTree.builder()
						.put("bar", "blob")
						.build())
				.build();
		ImmutableSet<?> resolved = resolver.resolve(tree, ImmutableList.of("foo","bar","width"));
		assertTrue(resolved.isEmpty());
	}

	private static <X> TypePropertiesLookup lookup(Class<X> clazz, TypeProperties<X> properties) {
		return new TypePropertiesLookup() {

			@Override
			public <T> Maybe<TypeProperties<T>> propertiesOf(Class<T> type) {
				if (clazz==type) {
					return Maybe.of((TypeProperties<T>) properties);
				}
				return Maybe.absent();
			}
		};
	}

	private static TypePropertyBasePropertyCollectionResolver resolver(TypePropertiesLookup lookup) {
		return new TypePropertyBasePropertyCollectionResolver(lookup);
	}

}
