package de.flapdoodle.solid.formatter;

import com.google.common.base.Preconditions;

import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.solid.types.tree.PropertyTree;

public class FormatterFactory {

	public static Formatter of(PropertyTree config) {
		Maybe<Formatter> formatter = UrlFormatter.parse(config)
			.or(() -> Maybe.absent());
		Preconditions.checkArgument(formatter.isPresent(),"could not find formatter for %s",config);
		return formatter.get();
	}
}
