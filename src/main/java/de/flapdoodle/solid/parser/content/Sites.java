package de.flapdoodle.solid.parser.content;

import com.google.common.base.Preconditions;

import de.flapdoodle.solid.formatter.DefaultObjectFormatter;
import de.flapdoodle.solid.generator.PathRenderer.FormatterOfProperty;
import de.flapdoodle.solid.types.Maybe;

public abstract class Sites {
	private Sites() {
		// no instance
	}

	public static FormatterOfProperty formatterOfProperty(Site site) {
		DefaultObjectFormatter defaultFormatter=new DefaultObjectFormatter();
		return (name,formatterName) -> {
			if (formatterName.isPresent()) {
				return Preconditions.checkNotNull(site.config().formatters().formatters().get(formatterName.get()),"could not get formatter %s",formatterName.get());
			}
			Maybe<String> defaultFormatterName = Maybe.ofNullable(site.config().defaultFormatter().get(name));
			if (defaultFormatterName.isPresent()) {
				return Preconditions.checkNotNull(site.config().formatters().formatters().get(defaultFormatterName.get()),"could not get formatter %s",defaultFormatterName.get());
			}
			return defaultFormatter;
		};
	}
}
