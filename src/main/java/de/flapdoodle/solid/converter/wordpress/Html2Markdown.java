package de.flapdoodle.solid.converter.wordpress;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.vladsch.flexmark.convert.html.FlexmarkHtmlParser;
import com.vladsch.flexmark.util.html.FormattingAppendable;
import com.vladsch.flexmark.util.html.FormattingAppendableImpl;
import com.vladsch.flexmark.util.options.MutableDataSet;

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
	
	public String convert(String src) {
    FormattingAppendableImpl out = new FormattingAppendableImpl(FormattingAppendable.SUPPRESS_TRAILING_WHITESPACE | FormattingAppendable.COLLAPSE_WHITESPACE);
    instance.parse(out, src);
    int maxBlankLines=3;
		return out.getText(maxBlankLines);
	}
	
}
