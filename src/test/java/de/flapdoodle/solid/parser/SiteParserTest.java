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
package de.flapdoodle.solid.parser;

import static org.junit.Assert.assertNotNull;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import de.flapdoodle.solid.parser.content.Site;

public class SiteParserTest {

	@Test
	public void siteParserWillCheckForSolidConfigFirst() {
		Path siteARoot = Paths.get("src", "test","resources","sample","site-a");
		SiteParser parser = SiteParser.parse(siteARoot);
		assertNotNull(parser);
		System.out.println(" -> "+parser);
		Site site = parser.collect();
		System.out.println(" -> "+site);
	}
}
