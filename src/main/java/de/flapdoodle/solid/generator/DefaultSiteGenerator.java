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
package de.flapdoodle.solid.generator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.parser.content.Site;

public class DefaultSiteGenerator implements SiteGenerator {

	@Override
	public ImmutableList<Document> generate(Site site) {
		System.out.println(" -> "+site);
		
		System.out.println("dates -> "+metaValues(site, "date"));
		System.out.println("titles -> "+metaValues(site, "title"));
		
		return ImmutableList.of();
	}

	private static List<Object> metaValues(Site site, String key) {
		List<Object> dates = site.blobs().stream()
			.map(blob -> blob.meta().find(Object.class, key))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.collect(Collectors.toList());
		return dates;
	}
}
