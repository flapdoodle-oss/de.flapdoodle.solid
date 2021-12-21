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
package de.flapdoodle.solid.types.tree;

import java.util.List;
import java.util.Set;

import com.google.common.base.Strings;

import de.flapdoodle.types.Either;

public class PropertyTreePrinter {
	
	private static final String INDENT= "  ";

	public static String prettyPrinted(PropertyTree map) {
		StringBuilder sb=new StringBuilder();
		sb.append("{\n");
		render(sb, map, 0);
		sb.append("}\n");
		return sb.toString();
	}
	
	private static void render(StringBuilder sb, PropertyTree map, int level) {
		String prefix=Strings.repeat(INDENT, level);
		
		Set<String> properties = map.properties();
		properties.forEach((key) -> {
			List<Either<Object, ? extends PropertyTree>> val = map.get(key);
			sb.append(prefix).append(INDENT).append(key).append("=[\n");
			val.forEach((Either<Object, ? extends PropertyTree> v) -> {
				if (v.isLeft()) {
					sb.append(prefix).append(INDENT).append(INDENT).append(v.left()).append(",\n");
				} else {
					sb.append(prefix).append(INDENT).append(INDENT).append("{\n");
					render(sb, v.right(), level+2);
					sb.append(prefix).append(INDENT).append(INDENT).append("},\n");
				}
			});
			sb.append(prefix).append(INDENT).append("],\n");
		});
	}

}
