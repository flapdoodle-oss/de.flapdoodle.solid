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
package de.flapdoodle.solid.parser;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.types.tree.FixedPropertyTree;
import de.flapdoodle.solid.types.tree.PropertyTree;

public class TreeTest {

	@Test
	public void parseSampleTree() {
		PropertyTree src=FixedPropertyTree.builder()
				.put("one", FixedPropertyTree.builder()
						.put("name", "A")
						.put("children", "A-a")
						.put("children", "A-b")
						.put("children", "A-c")
						.build())
				.put("two", FixedPropertyTree.builder()
						.put("name", "B")
						.build())
				.put("3", FixedPropertyTree.builder()
						.put("name", "A-a")
						.put("children", "A-a-0")
						.build())
				.build();
		
		Tree tree = Tree.treeOf(src);
		assertEquals("Tree{relation={A=[A-a, A-b, A-c], B=[], A-a=[A-a-0]}}",tree.toString());
		
		ImmutableList<Tree.Node> mappedTree = tree.mapAsTree(ImmutableList.of("A-a-0","C","A-a","B","A"));
		assertEquals("[Node{name=C, children=[]}, Node{name=B, children=[]}, Node{name=A, children=[Node{name=A-a, children=[Node{name=A-a-0, children=[]}]}]}]", mappedTree.toString());
	}

	@Test(expected=IllegalArgumentException.class)
	public void loopsNotAllowed() {
		PropertyTree src=FixedPropertyTree.builder()
				.put("one", FixedPropertyTree.builder()
						.put("name", "A")
						.put("children", "A-a")
						.put("children", "A-b")
						.put("children", "A-c")
						.build())
				.put("two", FixedPropertyTree.builder()
						.put("name", "B")
						.build())
				.put("3", FixedPropertyTree.builder()
						.put("name", "A-a")
						.put("children", "A")
						.build())
				.build();
		
		Tree.treeOf(src);
	}
}
