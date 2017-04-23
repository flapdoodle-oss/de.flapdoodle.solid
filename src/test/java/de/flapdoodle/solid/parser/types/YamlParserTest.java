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
package de.flapdoodle.solid.parser.types;

import java.io.IOException;

import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class YamlParserTest {
	@Test
	public void mapYaml() throws IOException {
		String yamlContent = Resources.asCharSource(Resources.getResource(getClass(), "sample.yaml"), Charsets.UTF_8).read();
		Object result = new Yaml().load(yamlContent);
		System.out.println(result);
		System.out.println(result.getClass());
	}

}
