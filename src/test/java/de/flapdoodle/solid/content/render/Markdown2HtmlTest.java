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
package de.flapdoodle.solid.content.render;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
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

	@Test
	public void markdownDoesNotChangePathInImageTag() {
		String html = asHtml("aaa\n" + 
				"\n" + 
				"[<img src=\"/uploads/thumb-cirinuela-herberge-4681.jpg\" border=\"0\" alt=\"Morgens vor der Herberge\" title=\"Morgens vor der Herberge\" width=\"450\" height=\"299\" />][1] \n" + 
				"\n" + 
				"bbb\n" + 
				"\n" + 
				" [1]: /uploads/cirinuela-herberge-4681.jpg\n" + 
				"");
		assertEquals("<p>aaa</p>\n" + 
				"<p><a href=\"/wp-content/uploads/cirinuela-herberge-4681.jpg\"><img src=\"/uploads/thumb-cirinuela-herberge-4681.jpg\" border=\"0\" alt=\"Morgens vor der Herberge\" title=\"Morgens vor der Herberge\" width=\"450\" height=\"299\" /></a></p>\n" + 
				"<p>bbb</p>\n" + 
				"", html);
	}

	@Test
	public void markdownDoesNotReplaceHtml() {
		String html = asHtml("<div style=\"text-align: center\">\n" + 
				"  <a href=\"/uploads/Logrono-Pilgerbrunnen-4514.jpg\"><img src=\"/uploads/thumb-Logrono-Pilgerbrunnen-4514.jpg\" border=\"0\" alt=\"Logrono Pilgerbrunnen\" title=\"Logrono Pilgerbrunnen\" width=\"300\" height=\"450\" /></a>\n" + 
				"</div>\n" + 
				"");
		assertEquals("<div style=\"text-align: center\">\n" + 
				"  <a href=\"/uploads/Logrono-Pilgerbrunnen-4514.jpg\"><img src=\"/uploads/thumb-Logrono-Pilgerbrunnen-4514.jpg\" border=\"0\" alt=\"Logrono Pilgerbrunnen\" title=\"Logrono Pilgerbrunnen\" width=\"300\" height=\"450\" /></a>\n" + 
				"</div>\n", html);
	}
	
	@Test
	@Ignore
	public void blockQuoteEscaping() {
		String html = asHtml("Da gab es ..." + 
				"\n" + 
				"> <meta content=\"text/html; charset=utf-8\" http-equiv=\"CONTENT-TYPE\" />\n" + 
				"> \n" + 
				"> <title />\n" + 
				"> \n" + 
				"\n\n");
		assertEquals("<p>Da gab es ...</p>\n" + 
				"<blockquote>\n" + 
				"  &amp;gt; &amp;lt;meta content=&amp;quot;text/html; charset=utf-8&amp;quot; http-equiv=&amp;quot;CONTENT-TYPE&amp;quot; /&amp;gt;\n" + 
				"  &amp;gt; \n" + 
				"  &amp;gt; &amp;lt;title /&amp;gt;\n" + 
				"</blockquote>\n", html);
	}
	
	@Test
	public void codeAsHtml() {
		String src="Text Text Text:\n" + 
				"\n" + 
				"\n" + 
				"```java5\n" + 
				"public class BookmarkablePageDestination<T extends WebPage> {\n" + 
				"```\n\n";
		
		String html = asHtml(src);
		assertEquals("<p>Text Text Text:</p>\n" + 
				"<pre><code class=\"java5\">public class BookmarkablePageDestination&lt;T extends WebPage&gt; {\n" + 
				"</code></pre>\n" + 
				"", html);
	}

	private static String asHtml(String markdown) {
		ImmutableRenderContext context = RenderContext.builder()
			.urlMapping(s -> {
				String prefix = "http://www.mosmann.de";
				if (s.startsWith(prefix)) {
					return Maybe.of(s.substring(prefix.length()));
				}
				if (s.startsWith("/uploads")) {
					return Maybe.of("/wp-content"+s);
				}
				return Maybe.of(s);
			})
			.build();
		return new Markdown2Html()
				.asHtml(context, markdown);
	}
}
