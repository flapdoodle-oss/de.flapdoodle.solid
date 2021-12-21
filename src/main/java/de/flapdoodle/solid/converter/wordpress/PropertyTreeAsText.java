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
package de.flapdoodle.solid.converter.wordpress;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.types.tree.PropertyTree;
import de.flapdoodle.types.Either;

public class PropertyTreeAsText {

	public static String asToml(PropertyTree tree) {
		StringBuilder sb=new StringBuilder();
		asToml(sb,tree, ImmutableList.of() ,0);
		return sb.toString();
	}

	private static void asToml(StringBuilder sb, PropertyTree tree, ImmutableList<String> parentKey, int skipIndent) {
		tree.forEach((key,values) -> {
			asToml(sb, parentKey, skipIndent, key, values);
		});
	}

	private static void asToml(StringBuilder sb, ImmutableList<String> parentKey, int skipIndent, String key, List<Either<Object, ? extends PropertyTree>> values) {
		ImmutableList<String> currentKey = append(parentKey, key);
		
		if (values.size()==1) {
			Either<Object, ? extends PropertyTree> either = values.get(0);
			if (either.isLeft()) {
				Object value = either.left();
				sb.append(indent(parentKey, skipIndent)).append(key).append(" = ").append(valueAsToml(value)).append("\n");
			} else {
				PropertyTree propertyTree = either.right();
				boolean renderTreeKey = !containsOnlyOtherPropertyTrees(propertyTree);
				if (renderTreeKey) {
					sb.append(indent(parentKey, skipIndent)).append("[").append(Joiner.on(".").join(currentKey)).append("]").append("\n");
				}
				asToml(sb, propertyTree, currentKey, renderTreeKey ? skipIndent : skipIndent+1);
			}
		} else {
			ImmutableList<Object> onlyValues = values.stream().filter(Either::isLeft).map(e -> e.left()).collect(ImmutableList.toImmutableList());
			ImmutableList<? extends PropertyTree> onlyTrees = values.stream().filter(e -> !e.isLeft()).map(e -> e.right()).collect(ImmutableList.toImmutableList());
			if (!onlyValues.isEmpty() && onlyTrees.isEmpty()) {
				sb.append(indent(parentKey, skipIndent)).append(key).append(" = ").append("[")
					.append(onlyValues.stream().map(v -> valueAsToml(v)).collect(Collectors.joining(", ")))
					.append("]\n");
			} else {
				if (onlyValues.isEmpty() && !onlyTrees.isEmpty()) {
					onlyTrees.forEach(t -> {
						asToml(sb, t, currentKey, skipIndent);
					});
				} else {
					throw new IllegalArgumentException("not supported: "+key+" = "+values);
				}
			}
		}
	}

	private static boolean containsOnlyOtherPropertyTrees(PropertyTree propertyTree) {
		Set<String> keys = propertyTree.properties();
		if (keys.size()==1) {
			List<Either<Object, ? extends PropertyTree>> values = propertyTree.get(keys.iterator().next());
			return !values.stream()
				.filter(e -> e.isLeft())
				.findAny().isPresent();
		}
		
		return false;
	}

	private static String indent(ImmutableList<String> parentKey, int skipIndent) {
		return Strings.repeat("\t", parentKey.size()-skipIndent);
	}

	private static ImmutableList<String> append(ImmutableList<String> parentKey, String key) {
		return ImmutableList.<String>builder().addAll(parentKey).add(key).build();
	}

	private static String valueAsToml(Object value) {
		if (value instanceof String) {
			return "\""+value+"\"";
		}
		return value.toString();
	}
}
