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
