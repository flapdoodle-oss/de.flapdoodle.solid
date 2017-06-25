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

public class TypePropertyBasePropertyResolverTest {

	@Test
	public void unknownProperty() {
		TypePropertyBasePropertyResolver resolver = resolver(lookup(Void.class, TypeProperties.builder(Void.class).build()));
		
		PropertyTree tree=FixedPropertyTree.builder()
				.put("foo", "bar")
				.build();
		Maybe<?> resolved = resolver.resolve(tree, ImmutableList.of("no"));
		assertFalse(resolved.isPresent());
	}
	
	@Test
	public void unknownSubProperty() {
		TypePropertyBasePropertyResolver resolver = resolver(lookup(Void.class, TypeProperties.builder(Void.class).build()));
		
		PropertyTree tree=FixedPropertyTree.builder()
				.put("foo", "bar")
				.build();
		Maybe<?> resolved = resolver.resolve(tree, ImmutableList.of("foo", "no"));
		assertFalse(resolved.isPresent());
	}
	
	@Test
	public void simpleProperty() {
		TypePropertyBasePropertyResolver resolver = resolver(lookup(Void.class, TypeProperties.builder(Void.class).build()));
		
		PropertyTree tree=FixedPropertyTree.builder()
				.put("foo", "bar")
				.build();
		Maybe<?> resolved = resolver.resolve(tree, ImmutableList.of("foo"));
		assertTrue(resolved.isPresent());
		assertEquals("bar", resolved.get());
	}
	
	@Test
	public void secondLevelProperty() {
		TypePropertyBasePropertyResolver resolver = resolver(lookup(Void.class, TypeProperties.builder(Void.class).build()));
		
		PropertyTree tree=FixedPropertyTree.builder()
				.put("foo", FixedPropertyTree.builder()
						.put("bar", "blob")
						.build())
				.build();
		Maybe<?> resolved = resolver.resolve(tree, ImmutableList.of("foo","bar"));
		assertTrue(resolved.isPresent());
		assertEquals("blob", resolved.get());
	}
	
	@Test
	public void secondLevelAndTypeProperty() {
		TypePropertyBasePropertyResolver resolver = resolver(lookup(String.class, TypeProperties.builder(String.class)
				.putMap("len", s -> s.length())
				.build()));
		
		PropertyTree tree=FixedPropertyTree.builder()
				.put("foo", FixedPropertyTree.builder()
						.put("bar", "blob")
						.build())
				.build();
		Maybe<?> resolved = resolver.resolve(tree, ImmutableList.of("foo","bar","len"));
		assertTrue(resolved.isPresent());
		assertEquals(4, resolved.get());
	}
	
	@Test
	public void secondLevelAndUnkownTypeProperty() {
		TypePropertyBasePropertyResolver resolver = resolver(lookup(String.class, TypeProperties.builder(String.class)
				.putMap("len", s -> s.length())
				.build()));
		
		PropertyTree tree=FixedPropertyTree.builder()
				.put("foo", FixedPropertyTree.builder()
						.put("bar", "blob")
						.build())
				.build();
		Maybe<?> resolved = resolver.resolve(tree, ImmutableList.of("foo","bar","width"));
		assertFalse(resolved.isPresent());
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
	
	private static TypePropertyBasePropertyResolver resolver(TypePropertiesLookup lookup) {
		return new TypePropertyBasePropertyResolver(lookup);
	}
}
