package de.flapdoodle.solid.site;

import static org.junit.Assert.assertEquals;

import java.util.function.Function;
import java.util.regex.Pattern;

import org.junit.Test;

import de.flapdoodle.solid.site.UrlRewrite.UrlRegex;
import de.flapdoodle.solid.types.Maybe;

public class UrlRewriteTest {

	@Test
	public void regexSample() {
		ImmutableUrlRegex regex = UrlRegex.builder()
				.name("sample")
				.pattern(Pattern.compile("/uploads/(?<all>(.+))"))
				.replacement("/wp-content/uploads/${all}")
				.build();

		assertEquals("/wp-content/uploads/foo.bar.jpg", regex.rewrite("/uploads/foo.bar.jpg"));
	}
	
	@Test
	public void rewriteSample() {
		ImmutableUrlRegex regex = UrlRegex.builder()
			.name("sample")
			.pattern(Pattern.compile("/uploads/(?<all>.+)"))
			.replacement("/wp-content/uploads/${all}")
			.build();
		
		Function<String, Maybe<String>> rewriter = UrlRewrite.builder()
			.addRegex(regex)
			.build().rewriter();
		
		assertEquals("/wp-content/uploads/foo.bar.jpg", rewriter.apply("/uploads/foo.bar.jpg").get());
	}
}
