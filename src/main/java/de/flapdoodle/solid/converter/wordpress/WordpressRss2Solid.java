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
package de.flapdoodle.solid.converter.wordpress;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.generator.Content;
import de.flapdoodle.solid.generator.Document;
import de.flapdoodle.solid.generator.Text;
import de.flapdoodle.solid.types.tree.FixedPropertyTree;
import de.flapdoodle.solid.types.tree.FixedPropertyTree.Builder;
import de.flapdoodle.solid.types.tree.ImmutableFixedPropertyTree;
import de.flapdoodle.solid.types.tree.PropertyTree;

public class WordpressRss2Solid {
	
	private static Html2Markdown HTML2MARKDOWN=Html2Markdown.newInstance();

	public static ImmutableList<Document> convert(WordpressRss src) {
		ImmutableList.Builder<Document> builder=ImmutableList.builder(); 
		
		builder.add(solidConfig(src));
		
		src.channel().items().stream()
			.filter(item -> item.isPost())
			.forEach(item -> {
				builder.add(asDocument(src, item));
			});
		
		src.channel().items().stream()
		.filter(item -> item.isPage())
		.forEach(item -> {
			builder.add(asDocument(src, item));
		});
		
		return builder.build();
	}

	private static Document solidConfig(WordpressRss src) {
		return Document.builder()
				.path("solid.toml")
				.content(solidConfigAsToml(src))
				.build();
	}

	private static Content solidConfigAsToml(WordpressRss src) {
		return Text.builder()
				.encoding(Charsets.UTF_8)
				.mimeType("text/text")
				.text(PropertyTreeAsText.asToml(solidConfigAsPropertyTree(src)))
				.build();
	}

	private static PropertyTree solidConfigAsPropertyTree(WordpressRss src) {
		WpChannel channel = src.channel();
		
		return FixedPropertyTree.builder()
				.put("baseURL", channel.baseUrl())
				.put("title", channel.title())
				.put("subtitle", channel.description())
				.put("languageCode", channel.language())
				.put("tree", categoryTree(channel.categories()))
				.build();
	}
	
	private static PropertyTree categoryTree(ImmutableList<WpCategory> categories) {
		return FixedPropertyTree.builder()
				.put("category", categoryTreeList(categories))
				.build();
	}

	private static PropertyTree categoryTreeList(ImmutableList<WpCategory> categories) {
		Builder builder = FixedPropertyTree.builder();
		
		categories.forEach(c -> {
			Builder entryBuilder = FixedPropertyTree.builder();
			entryBuilder.put("name", c.name());
			childNames(c, categories).forEach(n -> {
				entryBuilder.put("children", n);
			});
			ImmutableFixedPropertyTree entry = entryBuilder.build();
			builder.put(c.urlName(), entry);
		});
		
		return builder.build();
	}

	private static ImmutableList<String> childNames(WpCategory parent, ImmutableList<WpCategory> categories) {
		return categories.stream()
				.filter(c -> c.parent().isPresent() && c.parent().get().equals(parent.urlName()))
				.map(c -> c.name())
				.collect(ImmutableList.toImmutableList());
	}

	private static Document asDocument(WordpressRss src, WpItem item) {
		return MetaMarkdown.builder()
				.title(item.title())
				.author(item.author())
				.type(item.type())
				.date(format(item.date()))
				.url(urlOf(src, item))
				.isDraft(item.status().equals("draft"))
				
				.addAllCategories(item.categories().get("category"))
				.addAllTags(item.categories().get("post_tag"))
				.putAllMeta(item.meta())
				
				.path(pathOf(item))
				.body(asMarkDown(item.content()))
				.build()
				.asDocument();
	}
	
	private static String asMarkDown(String content) {
//		System.out.println("--------------------------");
//		System.out.println(content);
		return HTML2MARKDOWN.convert(content);
//		return content;
	}

	private static String urlOf(WordpressRss src, WpItem item) {
		String base = src.channel().baseUrl();
		if (item.link().startsWith(base)) {
			return item.link().substring(base.length());
		}
		return item.link();
	}

	private static final DateTimeFormatter POST_CREATED_PATTERN=DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss+00:00");
	
	// 2008-12-31T14:56:32+00:00
	private static String format(LocalDateTime date) {
		return date.format(POST_CREATED_PATTERN);
	}

	private static final DateTimeFormatter POST_FILE_PATTERN=DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private static String pathOf(WpItem item) {
		String prefix = item.isPage() ? "page/" : "post/";
 		return prefix + item.date().format(POST_FILE_PATTERN)+"-"+item.urlName()+".md";
	}
}
