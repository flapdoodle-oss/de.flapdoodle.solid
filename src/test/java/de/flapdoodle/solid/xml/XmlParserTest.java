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
package de.flapdoodle.solid.xml;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.dom4j.DocumentException;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;

public class XmlParserTest {

	@Test
	public void sampleWordpressVisitor() throws DocumentException, IOException {
		try (Reader reader = new InputStreamReader(Resources.asByteSource(Resources.getResource(getClass(), "wordpress-sample-rss.xml")).openBufferedStream())) {
			XmlParser.of(reader)
				.collect(v -> {
					System.out.println("visitor: "+v.current());
					v.visit(v2 -> {
						System.out.println("sub: "+v2.current());
					});
					return "Foo";
				});
		}
	}
	
	@Test
	@Ignore
	public void sampleWordpress() throws DocumentException, IOException {
		try (Reader reader = new InputStreamReader(Resources.asByteSource(Resources.getResource(getClass(), "wordpress-sample-rss.xml")).openBufferedStream())) {
			XmlParser.of(reader)
				.collect((parent,element) -> {
					System.out.println(parent);
					System.out.println(" - "+element);
					return ImmutableList.of();
				});
		}
	}
	
	@Test
	@Ignore
	public void sampleApi() throws DocumentException, IOException {
		XmlParser xmlParser = XmlParser.of(new StringReader("<foo><bar x=\"2\"></bar></foo>"));
		
		xmlParser.collect((parent,element) -> {
			System.out.println(element);
			return ImmutableList.of();
		});
	}
}
