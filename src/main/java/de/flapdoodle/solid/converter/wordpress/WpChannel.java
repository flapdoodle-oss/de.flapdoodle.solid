package de.flapdoodle.solid.converter.wordpress;

import org.immutables.value.Value.Immutable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.flapdoodle.solid.xml.Visitor;

/*
 * 	<title>wicket praxis</title>
	<link>http://www.wicket-praxis.de/blog</link>
	<description>erfahrungen mit wicket aus dem projektalltag</description>
	<pubDate>Sun, 16 Jul 2017 06:19:15 +0000</pubDate>
	<language>de-DE</language>
	<wp:wxr_version>1.2</wp:wxr_version>
	<wp:base_site_url>http://www.wicket-praxis.de/blog</wp:base_site_url>
	<wp:base_blog_url>http://www.wicket-praxis.de/blog</wp:base_blog_url>

 */
@Immutable
public abstract class WpChannel {
	public abstract String title();
	public abstract String description();
	public abstract String baseUrl();
	public abstract String language();
	
	public abstract ImmutableList<WpCategory> categories();
	public abstract ImmutableList<WpTag> tags();
	public abstract ImmutableList<WpTerm> terms();
	
	public abstract ImmutableList<WpItem> items();
	
	public static ImmutableWpChannel.Builder builder() {
		return ImmutableWpChannel.builder();
	}
	
	public static ImmutableList<WpChannel> channel(Visitor visitor) {
		if (visitor.currentTagName().equals("channel")) {
			ImmutableWpChannel.Builder builder = WpChannel.builder();
			visitor.visit(v -> channel().accept(v, builder));
			return ImmutableList.of(builder.build());
		}
		return ImmutableList.of();
	}
	
	private static VisitorConsumer<ImmutableWpChannel.Builder> channel() {
		ImmutableMap<String, VisitorConsumer<ImmutableWpChannel.Builder>> map = ImmutableMap.<String, VisitorConsumer<ImmutableWpChannel.Builder>>builder()
				.put("title", (visitor, builder) -> builder.title(visitor.dataAsType(String.class)))
				.put("description", (visitor, builder) -> builder.description(visitor.dataAsType(String.class)))
				.put("link", (visitor, builder) -> builder.baseUrl(visitor.dataAsType(String.class)))
				.put("language", (visitor, builder) -> builder.language(visitor.dataAsType(String.class)))
				.put("wp:category", (visitor, builder) -> WpCategory.read(visitor, builder::addCategories))
				.put("wp:tag", (visitor, builder) -> WpTag.read(visitor, builder::addTags))
				.put("wp:term", (visitor, builder) -> WpTerm.read(visitor, builder::addTerms))
				.put("item", (visitor, builder) -> WpItem.read(visitor, builder::addItems))
				.build();
		
		return VisitorConsumer.ofMap(map);
	}

}
