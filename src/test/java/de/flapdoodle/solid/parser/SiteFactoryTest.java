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
package de.flapdoodle.solid.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import de.flapdoodle.solid.content.render.MarkupRendererFactory;
import de.flapdoodle.solid.parser.content.BlobParser;
import de.flapdoodle.solid.parser.content.DefaultBlobParser;
import de.flapdoodle.solid.parser.content.Site;
import de.flapdoodle.solid.parser.types.FiletypeParserFactory;
import de.flapdoodle.solid.parser.types.PropertyTreeParserFactory;
import de.flapdoodle.solid.theme.ThemeFactory;

public class SiteFactoryTest {

	@Test
	public void siteParserWillCheckForSolidConfigFirst() {
		PropertyTreeParserFactory parserFactory = PropertyTreeParserFactory.defaultFactory();
		BlobParser blobParser = new DefaultBlobParser(parserFactory);
		MarkupRendererFactory markupRendererFactory=MarkupRendererFactory.defaultFactory();
		DefaultSiteFactory siteFactory = new DefaultSiteFactory(parserFactory, blobParser, ThemeFactory.defaultFactory(FiletypeParserFactory.defaults(parserFactory), markupRendererFactory));
		
		Path siteARoot = Paths.get("src", "test","resources","sample","site-a");
		Site site = siteFactory.siteOf(siteARoot);
		
		System.out.println(site.config().properties());
		
		assertNotNull(site);
		assertEquals(4, site.blobs().size());
	}
}
