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
package de.flapdoodle.solid.parser.config;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.moandjiezana.toml.Toml;

import de.flapdoodle.solid.types.PropertyTreeMap;

public class Toml2PropertyTreeMapTest {

	@Test
	public void mapToml() throws IOException {
		String tomlContent = Resources.asCharSource(Resources.getResource(getClass(), "sample.toml"), Charsets.UTF_8).read();
		Toml toml = new Toml().read(tomlContent);
		PropertyTreeMap treeMap = new Toml2PropertyTreeMap().asPropertyTreeMap(toml);
		assertEquals("ImmutablePropertyTreeMap{{date=2012-04-06, created=Tue Oct 24 23:04:33 CEST 2006, description=spf13-vim is a cross platform distribution of vim plugins and resources for Vim., categories=[Development, VIM], title=spf13-vim 3.0 release and new website, slug=spf13-vim-3-0-release-and-new-website, tags=[.vimrc, plugins, spf13-vim, vim]}}", treeMap.toString());
	}
}
