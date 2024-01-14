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
package de.flapdoodle.solid.parser.regex;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PatternsTest {

	@Test
	public void loopTest() {
		List<String> gaps=Lists.newArrayList();
		List<String> matches=Lists.newArrayList();
		
		Patterns.parse(Pattern.compile("(:(?<name>[a-zA-Z0-9]+))"), "/foo/bar/:property-nix/:other/nix", either -> {
			if (either.isLeft()) {
				gaps.add(either.left());
			} else {
				matches.add(either.right().group());
			}
		});
		
		assertEquals("[/foo/bar/, -nix/, /nix]", gaps.toString());
		assertEquals("[:property, :other]", matches.toString());
	}
}
