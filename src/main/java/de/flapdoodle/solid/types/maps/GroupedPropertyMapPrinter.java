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
package de.flapdoodle.solid.types.maps;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class GroupedPropertyMapPrinter {
	
	private static final String INDENT= "  ";

	public static String prettyPrinted(GroupedPropertyMap map) {
		StringBuilder sb=new StringBuilder();
		sb.append("{\n");
		render(sb, map, ImmutableList.of());
		sb.append("}\n");
		return sb.toString();
	}
	
	private static void render(StringBuilder sb, GroupedPropertyMap map, ImmutableList<String> current) {
		String prefix=Strings.repeat(INDENT, current.size());
		String[] array = current.toArray(new String[current.size()]);
		
		ImmutableMap<String, Object> properties = map.propertiesOf(array);
		properties.forEach((key,val) -> {
			sb.append(prefix).append(INDENT).append(key).append("=").append(val).append("\n");
		});
		
		ImmutableSet<String> groups = map.groupsOf(array);
		groups.forEach(g -> {
			sb.append(prefix).append(INDENT).append(g).append("=").append("{\n");
			render(sb,map,ImmutableList.<String>builder().addAll(current).add(g).build());
			sb.append(prefix).append(INDENT).append("}\n");
		});
	}


}
