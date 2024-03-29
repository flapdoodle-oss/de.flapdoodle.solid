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
package de.flapdoodle.solid.theme;

import de.flapdoodle.types.Pair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LinksTest {

	@Test
	public void stripDomainPart() {
		assertEquals(Pair.of("http://foo.bar", "/"), Links.splitDomainPart("http://foo.bar/"));
		assertEquals(Pair.of("http://foo.bar", "/bar"), Links.splitDomainPart("http://foo.bar/bar"));
		assertEquals(Pair.of("http://foo.bar", "/baz/nix"), Links.splitDomainPart("http://foo.bar/baz/nix"));
		assertEquals(Pair.of("http://foo.bar", "/"), Links.splitDomainPart("http://foo.bar"));
		assertEquals(Pair.of("", "/blub"), Links.splitDomainPart("/blub"));
	}
}
