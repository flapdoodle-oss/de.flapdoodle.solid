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
package de.flapdoodle.solid.parser.types;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import de.flapdoodle.solid.parser.meta.Toml;
import de.flapdoodle.solid.types.tree.PropertyTree;

public class Toml2PropertyTreeTest {

	@Test
	public void mapToml() throws IOException {
		String tomlContent = Resources.asCharSource(Resources.getResource(getClass(), "sample.toml"), Charsets.UTF_8).read();
		Toml toml = Toml.parse(tomlContent);
		PropertyTree propertyMap = new Toml2PropertyTree().asPropertyTree(toml);
		assertEquals(
			"FixedPropertyTree{map={date=[Left{left=2012-04-06}], created=[Left{left=Tue Oct 24 23:04:33 CEST 2006}], description=[Left{left=spf13-vim is a cross platform distribution of vim plugins and resources for Vim.}], categories=[Left{left=Development}, Left{left=VIM}], title=[Left{left=spf13-vim 3.0 release and new website}], slug=[Left{left=spf13-vim-3-0-release-and-new-website}], tags=[Left{left=.vimrc}, Left{left=plugins}, Left{left=spf13-vim}, Left{left=vim}]}}", propertyMap.toString());
	}

	@Test
	public void mapAllFeaturesToml() throws IOException {
		String tomlContent = Resources.asCharSource(Resources.getResource(getClass(), "features.toml"), Charsets.UTF_8).read();
		Toml toml = Toml.parse(tomlContent);
		PropertyTree propertyMap = new Toml2PropertyTree().asPropertyTree(toml);
		assertEquals(
			"FixedPropertyTree{map={sub=[Right{right=FixedPropertyTree{map={a=[Left{left=A}], sub=[Right{right=FixedPropertyTree{map={b=[Left{left=B}]}}}]}}}], string=[Left{left=content}], array=[Left{left=A}, Left{left=B}, Left{left=C}], created=[Left{left=Tue Oct 24 23:04:33 CEST 2006}], table=[Right{right=FixedPropertyTree{map={c=[Left{left=C}]}}}, Right{right=FixedPropertyTree{map={d=[Left{left=D}], json=[Right{right=FixedPropertyTree{map={i=[Right{right=FixedPropertyTree{map={j=[Left{left=J}], k=[Left{left=K}]}}}]}}}]}}}]}}", propertyMap.toString());
	}
}
