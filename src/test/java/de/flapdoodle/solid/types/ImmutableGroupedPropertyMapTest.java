package de.flapdoodle.solid.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class ImmutableGroupedPropertyMapTest {

	@Test
	public void simplePut() {
		ImmutableGroupedPropertyMap map = ImmutableGroupedPropertyMap.builder()
			.put("foo", "bar")
			.build();
		
		assertEquals("bar", map.get("foo").get());
		
		ImmutableMap<String, Object> properties = map.propertiesOf();
		assertEquals(ImmutableMap.of("foo","bar"), properties);
	}
	
	@Test
	public void multipleProperties() {
		ImmutableGroupedPropertyMap map = ImmutableGroupedPropertyMap.builder()
			.put("foo", "bar")
			.put("bar", "foo")
			.build();
		
		assertEquals("bar", map.get("foo").get());
		assertEquals("foo", map.get("bar").get());
		
		ImmutableMap<String, Object> properties = map.propertiesOf();
		assertEquals(ImmutableMap.of("foo","bar","bar","foo"), properties);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void overwriteMustFail() {
		ImmutableGroupedPropertyMap.builder()
			.put("foo", "bar")
			.put("foo", "blob")
			.build();
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void overwriteMustFailInGroup() {
		ImmutableGroupedPropertyMap.builder()
			.put("foo","sub", "bar")
			.put("foo","sub", "blob")
			.build();
	}
	
	@Test
	public void getNothingIfKeyDoesNotExistPut() {
		ImmutableGroupedPropertyMap map = ImmutableGroupedPropertyMap.builder()
			.put("foo", "bar")
			.build();
		
		assertFalse(map.get("bar").isPresent());
		assertFalse(map.get("bar","foo").isPresent());
	}
	
	@Test
	public void nestedPut() {
		ImmutableGroupedPropertyMap map = ImmutableGroupedPropertyMap.builder()
			.put("foo","blob", "bar")
			.build();
		
		
		assertEquals("bar", map.get("foo","blob").get());
		
		ImmutableMap<String, Object> properties = map.propertiesOf();
		assertEquals(ImmutableMap.of(), properties);
		
		properties = map.propertiesOf("foo");
		assertEquals(ImmutableMap.of("blob","bar"), properties);
		
		ImmutableSet<String> groups = map.groupsOf();
		assertEquals(ImmutableSet.of("foo"), groups);
		
		groups = map.groupsOf("foo");
		assertEquals(ImmutableSet.of(), groups);
	}
}
