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

import java.util.Optional;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import de.flapdoodle.solid.parser.Tree;
import de.flapdoodle.solid.site.PostProcessing;
import de.flapdoodle.solid.site.PostProcessing.BlobRegex;
import de.flapdoodle.solid.site.PostProcessing.Category;
import de.flapdoodle.solid.site.PostProcessing.Regex;
import de.flapdoodle.solid.site.SiteConfig;
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
	public Blob process(SiteConfig siteConfig, Blob src) {
		ImmutableBlob ret=ImmutableBlob.copyOf(src);
		if (!config.blobRegex().isEmpty()) {
			ret=ret.withContent(process(ret.content(), config.blobRegex()));
		}
		if (!config.regex().isEmpty()) {
			ret=ret.withMeta(process(ret.meta(),config.regex()));
		}
		if (!config.category().isEmpty()) {
			ret=ret.withMeta(process(ret.meta(),siteConfig, config.category()));
		}
		return ret;
	}

	private PropertyTree process(PropertyTree meta, SiteConfig siteConfig, ImmutableList<Category> categories) {
		Builder builder = FixedPropertyTree.builder()
				.copyOf(meta);

		for (PostProcessing.Category cat : categories) {
			Optional<Tree> tree = siteConfig.tree(cat.tree());
			tree.ifPresent(t -> {
				ImmutableList<String> sourceVal = meta.findList(String.class, Splitter.on(".").split(cat.source()));
				ImmutableSet<String> allParents = t.allParentsOf(sourceVal);
				ImmutableSet<String> allCategories = Sets.union(allParents, Sets.newHashSet(sourceVal)).immutableCopy();
				allCategories.forEach(c -> {
					builder.put(cat.name(), c);
				});
			});
		}

		return builder.build();
	}

	private static String process(String content, ImmutableList<BlobRegex> blobRegex) {
		String ret=content;
		for (BlobRegex r : blobRegex) {
			ret=r.pattern().matcher(ret).replaceAll(r.replacement());
		}
		return ret;
	}

	private static PropertyTree process(PropertyTree meta, ImmutableList<Regex> regex) {
		Builder builder = FixedPropertyTree.builder()
			.copyOf(meta);

		for (Regex r : regex) {
			Maybe<String> sourceVal = meta.find(String.class, Splitter.on(".").split(r.source()));
			if (sourceVal.isPresent()) {
				String source=sourceVal.get();
				String result = r.rewrite(source);
				builder.put(r.name(), result);
			}
		}

		return builder.build();
	}

	public static SiteConfigPostProcessor of(PostProcessing config) {
		return new SiteConfigPostProcessor(config);
	}
}
