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

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StaticHttpServerPageSinkTest {

	@Test
	public void resolvePathForUrl() {
		Path resolved = StaticHttpServerPageSink.resolve(Paths.get("foo", "bar"), "http:/foo.de/", "http:/foo.de/this/is/a/path/");
		assertEquals("foo/bar/this/is/a/path/index.html",resolved.toString());
	}
}
