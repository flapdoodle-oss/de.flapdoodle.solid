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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FixedPropertyTreeTest {

	@Test
	public void simpleMap() {
		PropertyTree tree = FixedPropertyTree.builder()
			.put("string", "Start")
			.put("number", 17)
			.build();
		
		assertEquals("[string, number]", tree.properties().toString());
		assertEquals("Start", tree.get("string").get(0).left());
		assertEquals(17, tree.get("number").iterator().next().left());
	}
	
	@Test
	public void subProperty() {
		PropertyTree tree = FixedPropertyTree.builder()
			.put("sub", FixedPropertyTree.builder()
					.build())
			.put("number", 17)
			.build();
		
		assertEquals("[sub, number]", tree.properties().toString());
		assertEquals("[]", tree.get("sub").get(0).right().properties().toString());
		assertEquals(17, tree.get("number").get(0).left());
	}
	
	@Test
	public void mixed() {
		PropertyTree tree = FixedPropertyTree.builder()
			.put("sub", FixedPropertyTree.builder()
					.put("foo", "bar")
					.build())
			.put("sub", 17)
			.build();
		
		assertEquals("[sub]", tree.properties().toString());
		assertEquals("[foo]", tree.get("sub").get(0).right().properties().toString());
		assertEquals(17, tree.get("sub").get(1).left());
		
		String pretty = PropertyTreePrinter.prettyPrinted(tree);
		System.out.println(pretty);
	}
	
	@Test
	public void copy() {
		PropertyTree org = FixedPropertyTree.builder()
			.put("sub", FixedPropertyTree.builder()
					.put("foo", "bar")
					.build())
			.put("sub", 17)
			.build();
		
		ImmutableFixedPropertyTree tree = FixedPropertyTree.builder().copyOf(org).build();
		
		assertEquals("[sub]", tree.properties().toString());
		assertEquals("[foo]", tree.get("sub").get(0).right().properties().toString());
		assertEquals(17, tree.get("sub").get(1).left());
		
		String pretty = PropertyTreePrinter.prettyPrinted(tree);
		System.out.println(pretty);
	}
}
