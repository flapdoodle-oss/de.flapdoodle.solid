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
package de.flapdoodle.solid.site;

import de.flapdoodle.solid.site.UrlRewrite.UrlRegex;
import de.flapdoodle.solid.types.Maybe;
import org.junit.jupiter.api.Test;

import java.util.function.Function;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
