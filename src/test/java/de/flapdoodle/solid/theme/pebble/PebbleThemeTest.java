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
