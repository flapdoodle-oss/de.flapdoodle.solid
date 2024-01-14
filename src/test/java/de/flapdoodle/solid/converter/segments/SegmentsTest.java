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
package de.flapdoodle.solid.converter.segments;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SegmentsTest {

	@Test
	public void noMatchWillCallDefaultTransformation() {
		String src="fooo <a>link</a> <b>bold</b> stuff";
		String result = Segments.map(src, s -> "["+s+"]", Matcher.of("X", "Y"));
		assertEquals("[fooo <a>link</a> <b>bold</b> stuff]", result);
	}
	
	@Test
	public void onMatch() {
		String src="fooo <a>link</a> <b>bold</b> stuff";
		String result = Segments.map(src, s -> "["+s+"]", Matcher.of("<a", "</a>"));
		assertEquals("[fooo ]<a>link</a>[ <b>bold</b> stuff]", result);
	}
	
	@Test
	public void everyThingIsAMatch() {
		String src="<a>aaa</a><b>bbb</b><c>ccc</c>";
		String result = Segments.map(src, s -> "#"+s+"#", Matcher.of("<a", "</a>",s -> "[A>"+s+"<]"), Matcher.of("<b", "</b>",s -> "[B>"+s+"<]"), Matcher.of("<c", "</c>",s -> "[C>"+s+"<]"));
		assertEquals("[A><a>aaa</a><][B><b>bbb</b><][C><c>ccc</c><]", result);
	}
	
	@Test
	public void findFirstBlock() {
		String src="fooo <a>link</a> <b>bold</b> stuff";
		assertEquals(Optional.of(Replacement.of(5, 16,"<a>link</a>")), Segments.firstOf(src, 0, Matcher.of("<a", "</a>"), Matcher.of("<b", "</b>")));
		assertEquals(Optional.of(Replacement.of(17, 28, "<b>bold</b>")), Segments.firstOf(src, 6, Matcher.of("<a", "</a>"), Matcher.of("<b", "</b>")));
		assertEquals(Optional.empty(), Segments.firstOf(src, 0, Matcher.of("<x", "</x>")));
	}

}
