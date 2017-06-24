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
package de.flapdoodle.solid.parser.content;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.site.PostProcessing;
import de.flapdoodle.solid.site.PostProcessing.Regex;
import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.solid.types.tree.FixedPropertyTree;
import de.flapdoodle.solid.types.tree.FixedPropertyTree.Builder;
import de.flapdoodle.solid.types.tree.PropertyTree;

public class SiteConfigPostProcessor implements PostProcessor {

	private final PostProcessing config;

	public SiteConfigPostProcessor(PostProcessing config) {
		this.config = config;
	}

	@Override
	public Blob process(Blob src) {
		if (!config.regex().isEmpty()) {
			return ImmutableBlob.copyOf(src)
					.withMeta(process(src.meta(),config.regex()));
		}
		return src;
	}

	private static PropertyTree process(PropertyTree meta, ImmutableList<Regex> regex) {
		Builder builder = FixedPropertyTree.builder()
			.copyOf(meta);
		
		for (Regex r : regex) {
			Maybe<String> sourceVal = meta.find(String.class, Splitter.on(".").split(r.source()));
			if (sourceVal.isPresent()) {
				String source=sourceVal.get();
				String result = r.pattern().matcher(source).replaceAll(r.replacement());
				builder.put(r.name(), result);
			}
		}
		
		return builder.build();
	}

	public static SiteConfigPostProcessor of(PostProcessing config) {
		return new SiteConfigPostProcessor(config);
	}
}
