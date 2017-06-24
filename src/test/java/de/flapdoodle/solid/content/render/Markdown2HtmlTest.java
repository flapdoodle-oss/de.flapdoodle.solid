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
package de.flapdoodle.solid.content.render;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.flapdoodle.solid.types.Maybe;

public class Markdown2HtmlTest {

	@Test
	public void simpleSample() {
		String html = asHtml("this is *fun*");
		assertEquals("<p>this is <em>fun</em></p>\n", html);
	}

	@Test
	public void headlineSample() {
		String html = asHtml("# H2\n\nthis is *fun*");
		assertEquals("<h1>H2</h1>\n" + 
				"<p>this is <em>fun</em></p>\n" + 
				"", html);
	}
	
	@Test
	public void htmlContentWithLinkSample() {
		String html = asHtml("* <div>\n" +
				"      <a href=\"http://www.chinesisch-lernen.org/\">Chinesisch-Lernen.org &#8211; Chinesisch lernen und China entdecken</a>\n" +
				"    </div>");

		assertEquals("<ul>\n" + 
				"  <li>\n" + 
				"  <div>\n" + 
				"    <a href=\"http://www.chinesisch-lernen.org/\">Chinesisch-Lernen.org &#8211; Chinesisch lernen und China entdecken</a>\n" + 
				"  </div>\n" + 
				"  </li>\n" + 
				"</ul>\n" + 
				"", html);
	}

	@Test
	public void linkSample() {
		String html = asHtml("Text\n" +
				"[Title][1]\n" +
				"\n" +
				"&nbsp;\n" +
				"\n" +
				" [1]: http://www.mosmann.de/blog/reiseberichte/jakobsweg-nach-santiago-de-compostela/logrono-ventosa-la-rioja/\n" +
				"");
		assertEquals("<p>Text\n" + 
				"<a href=\"/blog/reiseberichte/jakobsweg-nach-santiago-de-compostela/logrono-ventosa-la-rioja/\">Title</a></p>\n" + 
				"<p> </p>\n" + 
				"", html);
	}

	private static String asHtml(String markdown) {
		ImmutableRenderContext context = RenderContext.builder()
			.urlMapping(s -> {
				String prefix = "http://www.mosmann.de";
				if (s.startsWith(prefix)) {
					return Maybe.of(s.substring(prefix.length()));
				}
				return Maybe.of(s);
			})
			.build();
		return new Markdown2Html()
				.asHtml(context, markdown);
	}
}
