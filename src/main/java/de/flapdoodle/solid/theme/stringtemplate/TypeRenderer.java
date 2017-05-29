package de.flapdoodle.solid.theme.stringtemplate;

import java.util.Locale;

import org.stringtemplate.v4.AttributeRenderer;

public interface TypeRenderer<T> {
	String render(T o, String formatString, Locale locale);
	
	public static <T> TypeRenderer<T> asTypeRenderer(AttributeRenderer renderer) {
		return (instance,formatString,locale) -> renderer.toString(instance, formatString, locale);
	}
}