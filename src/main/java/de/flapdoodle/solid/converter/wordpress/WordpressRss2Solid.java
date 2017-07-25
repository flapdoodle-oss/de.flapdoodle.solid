package de.flapdoodle.solid.converter.wordpress;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.generator.Document;

public class WordpressRss2Solid {
	
	private static Html2Markdown HTML2MARKDOWN=Html2Markdown.newInstance();

	public static ImmutableList<Document> convert(WordpressRss src) {
		ImmutableList.Builder<Document> builder=ImmutableList.builder(); 
		
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
//		return HTML2MARKDOWN.convert(content);
		return content;
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
		return item.date().format(POST_FILE_PATTERN)+"-"+item.urlName()+".md";
	}
}
