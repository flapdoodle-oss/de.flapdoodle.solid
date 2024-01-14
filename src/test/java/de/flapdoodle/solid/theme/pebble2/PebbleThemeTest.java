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
package de.flapdoodle.solid.theme.pebble2;

import com.google.common.collect.ImmutableMap;
import com.mitchellbosecke.pebble.attributes.DynamicAttributeProviderResolver;
import de.flapdoodle.solid.theme.pebble2.CustomPebbleAttributeResolver;
import de.flapdoodle.solid.theme.pebble2.StringMapLoader;
import de.flapdoodle.solid.types.tree.FixedPropertyTree;
import de.flapdoodle.solid.types.tree.PropertyTree;
import de.flapdoodle.types.Try;
import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.attributes.AttributeResolver;
import io.pebbletemplates.pebble.attributes.ResolvedAttribute;
import io.pebbletemplates.pebble.error.LoaderException;
import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.loader.ClasspathLoader;
import io.pebbletemplates.pebble.loader.Loader;
import io.pebbletemplates.pebble.node.ArgumentsNode;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PebbleThemeTest {

	@Test
	public void sample() throws PebbleException, IOException {
		Loader<String> loader=new Loader<String>() {

			@Override
			public boolean resourceExists(String templateName) {
				return true;
			}

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
		
		PebbleTemplate template = engineWithExtensions(loader).getTemplate("sample.html");
		
		StringWriter result = new StringWriter();
		template.evaluate(result, ImmutableMap.of("foo",new Fake(ImmutableMap.of("bar", "bar"))));
		assertEquals("-->bar<--", result.toString());
		
	}
	
	@Test
	public void testPropertyTreeSupport() throws PebbleException, IOException {
		ClasspathLoader loader = new ClasspathLoader();
		loader.setPrefix(getClass().getPackage().getName().replace('.', '/'));
		
		PebbleEngine engine = engineWithExtensions(loader);
		
		PebbleTemplate template = engine.getTemplate("sample.html");
		StringWriter result = new StringWriter();
		PropertyTree propertyTree = FixedPropertyTree.builder()
			.put("bar", "bar")
			.build();
		template.evaluate(result, ImmutableMap.of("foo",propertyTree));
		assertEquals("-->[bar]<--", result.toString());
	}

	@Test
	public void testMethodCall() {
		StringMapLoader loader=new StringMapLoader(ImmutableMap.of("sample.html","{{ foo.html(2) }}"));
		PebbleEngine engine = engineWithExtensions(loader);
		String result = render(engine, "sample.html", ImmutableMap.of("foo",new GetMethodSample()));
		assertEquals("html with 2", result.toString());
	}

	@Test
	public void propertyOfProperty() {
		StringMapLoader loader=new StringMapLoader(ImmutableMap.of("sample.html","{{ it.foo.bar }}"));
		PebbleEngine engine = engineWithExtensions(loader);
		String result = render(engine, "sample.html", ImmutableMap.of("it",new It()));
		assertEquals("baz", result.toString());
	}

	public static class It {
		public Foo foo() {
			return new Foo();
		}
	}

	public static class Foo {
		public String bar() {
			return "baz";
		}
	}


	public static class GetMethodSample {
		public String getHtml(int offset) {
			return "html with "+offset;
		}
	}
	
	public static class Fake implements DynamicAttributeResolver {

		private final ImmutableMap<String, Object> values;

		public Fake(ImmutableMap<String, Object> values) {
			this.values = values;
		}
		
//		@Override
//		public ResolvedAttribute resolve(Object attributeNameValue, Object[] argumentValues, ArgumentsNode args, EvaluationContextImpl context,
//			String filename, int lineNumber) {
//			Object value = values.get(attributeNameValue);
//			return value != null ? new ResolvedAttribute(value) : null;
//		}

		@Override
		public boolean canProvideDynamicAttribute(Object attributeName) {
			return values.containsKey(attributeName);
		}

		@Override
		public Object getDynamicAttribute(Object attributeName, Object[] argumentValues) {
			return values.get(attributeName);
		}

	}
	
	private static PebbleEngine engineWithExtensions(Loader loader) {
		return new PebbleEngine.Builder()
			.loader(loader)
			.extension(
				new CustomPebbleAttributeResolver().asExtension(),
				new Delegate2DynamicAttributeResolver().asExtension(),
				new MethodResolver().asExtension()
			)
			.build();
	}
	
	private static String render(PebbleEngine engine, String templateName, Map<String, Object> context) {
		return Try.supplier(() -> {
				PebbleTemplate template = engine.getTemplate(templateName);
				StringWriter result = new StringWriter();
				template.evaluate(result, context);
				return result.toString();
			})
			.mapToUncheckedException(RuntimeException::new)
			.get();
	}
	
}
