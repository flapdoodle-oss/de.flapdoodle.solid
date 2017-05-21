package de.flapdoodle.solid.types.reflection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.common.collect.ImmutableSet;

public class InspectorTest {

	@Test
	public void propertyOfStringIsEmpty() {
		ImmutableSet<String> result = Inspector.propertyNamesOf(String.class);
		assertTrue(""+result, result.isEmpty());
	}
	
	@Test
	public void propertyOfSample() {
		ImmutableSet<String> result = Inspector.propertyNamesOf(Sample.class);
		assertEquals(ImmutableSet.of("getter","boolean","justAName"),result);
	}
	
	@Test
	public void propertyOfSubSample() {
		ImmutableSet<String> result = Inspector.propertyNamesOf(SubSample.class);
		assertEquals(ImmutableSet.of("getter","boolean","justAName","otherStuff"),result);
	}
	
	@Test
	public void propertyOfSubSampleType() {
		ImmutableSet<String> result = Inspector.propertyNamesOf(SubSampleType.class);
		assertEquals(ImmutableSet.of("getter","boolean","justAName","sub","base"),result);
	}
	
	interface Sample {
		String getGetter();
		boolean isBoolean();
		String justAName();
	}
	
	interface SubSample extends Sample {
		String otherStuff();
	}
	
	abstract class SampleType {
		public abstract String base();
	}
	
	abstract class SubSampleType extends SampleType implements Sample {
		@Override
		public abstract String base();
		public abstract String sub();
	}
}
