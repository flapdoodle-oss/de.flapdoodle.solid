package de.flapdoodle.solid.site;

import java.util.List;

import org.immutables.value.Value.Immutable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import de.flapdoodle.solid.formatter.Formatter;
import de.flapdoodle.solid.formatter.FormatterFactory;
import de.flapdoodle.solid.formatter.StringFormatFormatter;
import de.flapdoodle.solid.site.ImmutableFormatters.Builder;
import de.flapdoodle.solid.types.tree.PropertyTree;
import de.flapdoodle.types.Either;

@Immutable
public interface Formatters {
	
	ImmutableMap<String, Formatter> formatters();

	public static Formatters of(PropertyTree tree) {
		Builder builder = ImmutableFormatters.builder();
		tree.properties().forEach(name -> {
			List<Either<Object, ? extends PropertyTree>> entries = tree.get(name);
			Preconditions.checkArgument(entries.size()==1,"more or less then one entry for %s",name);
			Either<Object, ? extends PropertyTree> formatterConfig = entries.get(0);
			if (formatterConfig.isLeft()) {
				Object formatterParameter = formatterConfig.left();
				Preconditions.checkArgument(formatterParameter instanceof String,"could not parse %s for %s",formatterParameter,name);
				builder.putFormatters(name, new StringFormatFormatter((String) formatterParameter));
			} else {
				builder.putFormatters(name, FormatterFactory.of(formatterConfig.right()));
			}
		});
		return builder.build();
	}

	static Formatters empty() {
		return ImmutableFormatters.builder().build();
	}
}
