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
package de.flapdoodle.solid.generator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import de.flapdoodle.solid.formatter.DefaultObjectFormatter;
import de.flapdoodle.solid.generator.PathRenderer.FormatterOfProperty;
import de.flapdoodle.solid.parser.path.Path;
import de.flapdoodle.solid.types.Maybe;

public class DefaultPathRendererTest {

	@Test
	public void pagePathShouldOmitFirstPage() {
		DefaultPathRenderer renderer = new DefaultPathRenderer("base/");
		FormatterOfProperty formatter=(property, formatterName) -> new DefaultObjectFormatter();
		Maybe<String> result = renderer.render(Path.parse("foo/:bar/:page/"), ImmutableMap.of("bar","baz","page",1), formatter);
		assertEquals("base/foo/baz/",result.get());
	}

	@Test
	public void doublePropertyShouldRender() {
		DefaultPathRenderer renderer = new DefaultPathRenderer("base/");
		FormatterOfProperty formatter=(property, formatterName) -> new DefaultObjectFormatter();
		Maybe<String> result = renderer.render(Path.parse("foo/:bar/:page/"), ImmutableMap.of("bar",3.0,"page",1), formatter);
		assertEquals("base/foo/3.0/",result.get());
	}
}
