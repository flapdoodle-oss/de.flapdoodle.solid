package de.flapdoodle.solid.converter.wordpress;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.regex.Pattern;

import com.google.common.annotations.VisibleForTesting;
import com.vladsch.flexmark.convert.html.FlexmarkHtmlParser;
import com.vladsch.flexmark.util.html.FormattingAppendable;
import com.vladsch.flexmark.util.html.FormattingAppendableImpl;
import com.vladsch.flexmark.util.options.MutableDataSet;

import de.flapdoodle.solid.converter.segments.Matcher;
import de.flapdoodle.solid.converter.segments.Replacement;
import de.flapdoodle.solid.converter.segments.Segments;

public class Html2Markdown {

	private final FlexmarkHtmlParser instance;

	private Html2Markdown() throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Constructor<FlexmarkHtmlParser> constructor = (Constructor<FlexmarkHtmlParser>) FlexmarkHtmlParser.class.getDeclaredConstructors()[0];
		constructor.setAccessible(true);
		this.instance = constructor.newInstance(new MutableDataSet());
	}
	
	public static Html2Markdown newInstance() {
		try {
			return new Html2Markdown();
		}
		catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static String codeAsMarkdown(String code, String language) {
		StringBuilder sb=new StringBuilder();
		sb.append("\n```").append(language).append("\n");
		sb.append(code);
		sb.append("\n```\n\n");
		return sb.toString();
	}
	
	public String convert(String src) {
		return Segments.map(src, this::asMarkdown, codeMatcher("pre", "lang"), codeMatcher("code", "lang"), Matcher.of("<!--", "-->", s -> s));
	}

	private String asMarkdown(String src) {
		FormattingAppendableImpl out = new FormattingAppendableImpl(FormattingAppendable.SUPPRESS_TRAILING_WHITESPACE | FormattingAppendable.COLLAPSE_WHITESPACE);
    instance.parse(out, src);
    int maxBlankLines=3;
		return out.getText(maxBlankLines);
	}
	
	private static class CodeTagMatcher implements Matcher {
		
		private final String tagName;
		private final String languageAttribute;
		private final Pattern startPattern;
		private final Pattern endPattern;

		public CodeTagMatcher(String tagName, String languageAttribute) {
			this.tagName = tagName;
			this.languageAttribute = languageAttribute;
			this.startPattern = Pattern.compile("<"+tagName+"\\s+"+languageAttribute+"\\s*=\\s*\"(?<language>[^\"]*)\"\\s*>");
			this.endPattern = Pattern.compile("</"+tagName+">");
		}

		@Override
		public Optional<Replacement> find(String src, int currentPosition) {
			java.util.regex.Matcher matcher = startPattern.matcher(src);
			if (matcher.find(currentPosition)) {
				java.util.regex.Matcher endMatcher = endPattern.matcher(src);
				if (endMatcher.find(matcher.end())) {
					String code=src.substring(matcher.end(), endMatcher.start());
					return Optional.of(Replacement.of(matcher.start(), endMatcher.end(), codeAsMarkdown(code, matcher.group("language"))));
				}
			}
			return Optional.empty();
		}
		
	}
	
	@VisibleForTesting
	protected static Matcher codeMatcher(String tagName, String languageAttribute) {
		return new CodeTagMatcher(tagName, languageAttribute);
	}
}
