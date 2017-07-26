package de.flapdoodle.solid.converter.segments;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;

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
