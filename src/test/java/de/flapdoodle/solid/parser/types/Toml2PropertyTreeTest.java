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
		assertEquals("FixedPropertyTree{map={date=[Either{optLeft=2012-04-06}], created=[Either{optLeft=Tue Oct 24 23:04:33 CEST 2006}], description=[Either{optLeft=spf13-vim is a cross platform distribution of vim plugins and resources for Vim.}], categories=[Either{optLeft=Development}, Either{optLeft=VIM}], title=[Either{optLeft=spf13-vim 3.0 release and new website}], slug=[Either{optLeft=spf13-vim-3-0-release-and-new-website}], tags=[Either{optLeft=.vimrc}, Either{optLeft=plugins}, Either{optLeft=spf13-vim}, Either{optLeft=vim}]}}", propertyMap.toString());
	}

	@Test
	public void mapAllFeaturesToml() throws IOException {
		String tomlContent = Resources.asCharSource(Resources.getResource(getClass(), "features.toml"), Charsets.UTF_8).read();
		Toml toml = Toml.parse(tomlContent);
		PropertyTree propertyMap = new Toml2PropertyTree().asPropertyTree(toml);
		assertEquals("FixedPropertyTree{map={sub=[Either{optRight=FixedPropertyTree{map={a=[Either{optLeft=A}], sub=[Either{optRight=FixedPropertyTree{map={b=[Either{optLeft=B}]}}}]}}}], string=[Either{optLeft=content}], array=[Either{optLeft=A}, Either{optLeft=B}, Either{optLeft=C}], created=[Either{optLeft=Tue Oct 24 23:04:33 CEST 2006}], table=[Either{optRight=FixedPropertyTree{map={c=[Either{optLeft=C}]}}}, Either{optRight=FixedPropertyTree{map={d=[Either{optLeft=D}], json=[Either{optRight=FixedPropertyTree{map={i=[Either{optRight=FixedPropertyTree{map={j=[Either{optLeft=J}], k=[Either{optLeft=K}]}}}]}}}]}}}]}}", propertyMap.toString());
	}
}
