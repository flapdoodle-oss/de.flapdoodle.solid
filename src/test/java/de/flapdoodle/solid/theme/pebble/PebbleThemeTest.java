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
package de.flapdoodle.solid.theme.pebble;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.LoaderException;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.DynamicAttributeProvider;
import com.mitchellbosecke.pebble.loader.ClasspathLoader;
import com.mitchellbosecke.pebble.loader.Loader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

public class PebbleThemeTest {

	@Test
	public void sample() throws PebbleException, IOException {
		Loader<String> loader=new Loader<String>() {

			@Override
			public Reader getReader(String cacheKey) throws LoaderException {
				return new StringReader("{{ foo }}");
			}

			@Override
			public void setCharset(String charset) {
				throw new IllegalArgumentException("what?");
			}

			@Override
			public void setPrefix(String prefix) {
				throw new IllegalArgumentException("what?");
			}

			@Override
			public void setSuffix(String suffix) {
				throw new IllegalArgumentException("what?");
			}

			@Override
			public String resolveRelativePath(String relativePath, String anchorPath) {
				return "fuuu";
			}

			@Override
			public String createCacheKey(String templateName) {
				return templateName;
			}
			
		};
		PebbleEngine engine = new PebbleEngine.Builder()
				.loader(loader)
				.build();
		
		PebbleTemplate template = engine.getTemplate("sample.html");
		
		StringWriter writer = new StringWriter();
		Map<String, Object> context=ImmutableMap.of("foo","what?");
		template.evaluate(writer, context, Locale.GERMANY);
		
		String result = writer.toString();
		assertEquals("what?",result);
	}
	
	@Test
	public void testSomeStuff() throws PebbleException, IOException {
		ClasspathLoader loader = new ClasspathLoader();
		loader.setPrefix(getClass().getPackage().getName().replace('.', '/'));
		
		PebbleEngine engine = new PebbleEngine.Builder()
			.loader(loader)
			.build();
		
		PebbleTemplate template = engine.getTemplate("sample.html");
		
		StringWriter result = new StringWriter();
		template.evaluate(result, ImmutableMap.of("foo",new Fake(ImmutableMap.of("bar", "bar"))));
		assertEquals("-->bar<--", result.toString());
		
	}
	
	public static class Fake implements DynamicAttributeProvider {

		private final ImmutableMap<String, Object> values;

		public Fake(ImmutableMap<String, Object> values) {
			this.values = values;
		}
		
		@Override
		public boolean canProvideDynamicAttribute(Object attributeName) {
			return values.containsKey(attributeName);
		}

		@Override
		public Object getDynamicAttribute(Object attributeName, Object[] argumentValues) {
			return values.get(attributeName);
		}
		
	}
	
}
