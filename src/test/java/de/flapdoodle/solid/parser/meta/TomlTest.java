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
package de.flapdoodle.solid.parser.meta;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import de.flapdoodle.solid.parser.types.Toml2GroupedPropertyMap;
import de.flapdoodle.solid.types.maps.GroupedPropertyMap;

public class TomlTest {

	@Test
	public void arraysOfTableSample() throws IOException {
		String source=Resources.toString(Resources.getResource(getClass(), "arraysOfTable.toml"), Charsets.UTF_8);
		Toml toml = Toml.parse(source);
		Map<String, Object> asMap = toml.asMap();
		System.out.println("map -> "+asMap);
		
		GroupedPropertyMap groupedMap = new Toml2GroupedPropertyMap().asGroupedPropertyMap(toml);
		System.out.println("map -> "+groupedMap.prettyPrinted());
	}
}
