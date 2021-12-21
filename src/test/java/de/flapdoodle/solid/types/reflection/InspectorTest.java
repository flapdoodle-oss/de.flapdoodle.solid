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
