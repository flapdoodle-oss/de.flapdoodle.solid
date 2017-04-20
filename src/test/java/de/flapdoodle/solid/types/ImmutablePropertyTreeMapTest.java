package de.flapdoodle.solid.types;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

public class ImmutablePropertyTreeMapTest {

	@Test
	public void simplePut() {
		ImmutableMap<String, Object> result = ImmutablePropertyTreeMap.builder()
			.put("foo", "bar")
			.build().asMap();
		
		assertEquals(ImmutableMap.of("foo","bar"),result);
	}
	
	@Test
	public void simpleTree() {
		ImmutableMap<String, Object> result = ImmutablePropertyTreeMap.builder()
			.put("foo", "bar")
			.start("blob")
			.end()
			.build().asMap();
		
		assertEquals(ImmutableMap.of("foo","bar","blob",ImmutableMap.of()),result);
	}

	@Test
	public void putAList() {
		ImmutableMap<String, Object> result = ImmutablePropertyTreeMap.builder()
			.put("foo", Lists.newArrayList("12",3,3d,'c',Lists.newArrayList("subsub")))
			.build().asMap();
		
		assertEquals(ImmutableMap.of("foo",ImmutableList.of("12",3,3d,'c',ImmutableList.of("subsub"))),result);
	}
}
