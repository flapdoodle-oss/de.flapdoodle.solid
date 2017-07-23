package de.flapdoodle.solid.converter.wordpress;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.converter.wordpress.ImmutableWordpressRss.Builder;
import de.flapdoodle.solid.xml.Visitor;
import de.flapdoodle.solid.xml.XmlParser;

@SuppressWarnings("ucd")
public class WordpressRssConverter {

	public static void main(String[] args) {
		System.out.println("solid wordpress converter");
		Preconditions.checkArgument(args.length>=2,"usage: <siteRoot> <exportDirectory>");
		
		Path siteRoot = Paths.get(args[0]);
		Path target = Paths.get(args[1]);

		
	}
	
	@VisibleForTesting
	protected static WordpressRss build(XmlParser xml) {
		return xml.collect(WordpressRssConverter::root);
	}
	
	private static WordpressRss root(Visitor visitor) {
		Builder builder = WordpressRss.builder();
		ImmutableList<WpChannel> channels = visitor.collect(WpChannel::channel);
		Preconditions.checkArgument(channels.size()==1,"more or less then one channel: %s",channels);
		builder.channel(channels.get(0));
		return builder.build();
	}

	public static LocalDateTime parseWpDate(String src) {
		return LocalDateTime.parse(src, WordpressRssConverter.DEFAULT_FORMAT);
	}

	public static final DateTimeFormatter DEFAULT_FORMAT=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	

}
