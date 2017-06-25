package de.flapdoodle.solid.generator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

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
		ImmutableList<?> resolved = resolver.resolve(tree, ImmutableList.of("no"));
		assertTrue(resolved.isEmpty());
	}
	
	@Test
	public void unknownSubProperty() {
		TypePropertyBasePropertyCollectionResolver resolver = resolver(lookup(Void.class, TypeProperties.builder(Void.class).build()));
		
		PropertyTree tree=FixedPropertyTree.builder()
				.put("foo", "bar")
				.build();
		ImmutableList<?> resolved = resolver.resolve(tree, ImmutableList.of("foo", "no"));
		assertTrue(resolved.isEmpty());
	}
	
	@Test
	public void simpleProperty() {
		TypePropertyBasePropertyCollectionResolver resolver = resolver(lookup(Void.class, TypeProperties.builder(Void.class).build()));
		
		PropertyTree tree=FixedPropertyTree.builder()
				.put("foo", "bar")
				.build();
		ImmutableList<?> resolved = resolver.resolve(tree, ImmutableList.of("foo"));
		assertFalse(resolved.isEmpty());
		assertEquals(ImmutableList.of("bar"), resolved);
	}
	
	@Test
	public void secondLevelProperty() {
		TypePropertyBasePropertyCollectionResolver resolver = resolver(lookup(Void.class, TypeProperties.builder(Void.class).build()));
		
		PropertyTree tree=FixedPropertyTree.builder()
				.put("foo", FixedPropertyTree.builder()
						.put("bar", "blob")
						.build())
				.build();
		ImmutableList<?> resolved = resolver.resolve(tree, ImmutableList.of("foo","bar"));
		assertFalse(resolved.isEmpty());
		assertEquals(ImmutableList.of("blob"), resolved);
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
		ImmutableList<?> resolved = resolver.resolve(tree, ImmutableList.of("foo","bar","len"));
		assertFalse(resolved.isEmpty());
		assertEquals(ImmutableList.of(4), resolved);
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
		ImmutableList<?> resolved = resolver.resolve(tree, ImmutableList.of("foo","bar","len"));
		assertFalse(resolved.isEmpty());
		assertEquals(ImmutableList.of(1,2,3), resolved);
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
		ImmutableList<?> resolved = resolver.resolve(tree, ImmutableList.of("foo","bar","width"));
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
