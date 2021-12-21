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
package de.flapdoodle.solid.sinks;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.flapdoodle.solid.types.Pair;

public class UndertowPageSinkTest {

	@Test
	public void hostAndPath() {
		Pair<String, String> hostAndBasePath = UndertowPageSink.hostAndBasePath("http://fooo.bar:1234/path");
		assertEquals("http://fooo.bar:1234",hostAndBasePath.a());
		assertEquals("/path/",hostAndBasePath.b());
	}
}
