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
package de.flapdoodle.solid.parser.content;

import org.immutables.value.Value;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import de.flapdoodle.solid.generator.Document;
import de.flapdoodle.solid.site.SiteConfig;
import de.flapdoodle.solid.theme.Theme;

@Value.Immutable
public interface Site {
	SiteConfig config();
	Theme theme();

	ImmutableList<Blob> blobs();
	ImmutableSet<String> ignoredFiles();
	ImmutableList<Document> staticFiles();

	public static ImmutableSite.Builder builder() {
		return ImmutableSite.builder();
	}
}
