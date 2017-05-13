package de.flapdoodle.solid.theme.mustache;

import java.io.StringReader;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Mustache.Formatter;
import com.samskivert.mustache.Mustache.TemplateLoader;

public class MustacheThemeTest {
	@Test
	public void simpleSample() {
		String result = Mustache.compiler()
				.compile("Huii {{foo.bar}}")
				.execute(ImmutableMap.of("foo", ImmutableMap.of("bar",17)));
		
		System.out.println(result);
	}
	
	@Test
	public void partial() {
		Formatter formatter=value -> value.toString();
		
		TemplateLoader loader=name -> new StringReader(">{{foo.bar}}<");
		
//		Mustache.VariableFetcher;
		
		String result = Mustache.compiler()
				.emptyStringIsFalse(true)
				.withFormatter(formatter)
				.withLoader(loader)
				.compile("Huii {{> sub}}")
				.execute(ImmutableMap.of("foo", ImmutableMap.of("bar",17)));
		
		System.out.println(result);
	}
}
