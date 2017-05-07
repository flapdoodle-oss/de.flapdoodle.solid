package de.flapdoodle.solid.parser.regex;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.regex.Pattern;

import org.junit.Test;

import com.google.common.collect.Lists;

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
