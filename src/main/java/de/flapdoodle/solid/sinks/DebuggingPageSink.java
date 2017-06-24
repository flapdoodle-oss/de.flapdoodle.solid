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
package de.flapdoodle.solid.sinks;

import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.PageSink;
import de.flapdoodle.solid.generator.Document;
import de.flapdoodle.solid.generator.Text;
import de.flapdoodle.solid.site.SiteConfig;

public final class DebuggingPageSink implements PageSink {
	
	private final boolean showContent;

	public DebuggingPageSink(boolean showContent) {
		this.showContent = showContent;
	}
	
	@Override
	public void accept(SiteConfig siteConfig, ImmutableList<Document> documents) {
		if (!documents.isEmpty()) {
			System.out.println("-------------------------");
			System.out.println("Documents: ");
			documents.forEach(d -> {
				System.out.println(" - "+d.path());
			});
			System.out.println("-------------------------");
			if (showContent) {
				documents.forEach(d -> {
				if (d.content() instanceof Text) {
					System.out.println(" - "+d.path());
					System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
					System.out.println(((Text) d.content()).text());
					System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
				}
			});
			}

		} else {
			System.out.println("-------------------------");
			System.out.println("No generated Documents.");
			System.out.println("-------------------------");
		}
	}
}