package de.flapdoodle.solid.theme.stringtemplate;

import java.util.Locale;
import java.util.function.Function;

import de.flapdoodle.solid.formatter.Formatter;
import de.flapdoodle.solid.types.Maybe;

public class FormatterRendererAdapter<T> implements TypeRenderer<T> {

	private final Function<String, Maybe<Formatter>> formatterOfName;
	private final TypeRenderer<T> fallback;

	private FormatterRendererAdapter(Function<String, Maybe<Formatter>> formatterOfName, TypeRenderer<T> fallback) {
		this.formatterOfName = formatterOfName;
		this.fallback = fallback;
	}
	
	@Override
	public String render(T o, String formatString, Locale locale) {
		return formatterOfName.apply(formatString)
			.flatMap(f -> f.format(o))
			.orElse(() -> fallback.render(o, formatString, locale));
	}

	public static <T> FormatterRendererAdapter<T> of(Function<String, Maybe<Formatter>> formatterOfName, TypeRenderer<T> fallback) {
		return new FormatterRendererAdapter<>(formatterOfName, fallback);
	}
}
