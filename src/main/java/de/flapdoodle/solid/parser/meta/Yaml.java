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
package de.flapdoodle.solid.parser.meta;

import java.util.Map;

public class Yaml implements AsMap {

	private final Map<String, Object> map;

	public Yaml(Map<String, Object> map) {
		this.map = map;
	}

	@Override
	public Map<String, Object> asMap() {
		return map;
	}

	public static Yaml parse(String content) {
		return new Yaml((Map<String, Object>) new org.yaml.snakeyaml.Yaml().load(content));
	}
}
