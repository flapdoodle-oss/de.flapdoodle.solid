package de.flapdoodle.solid.generator;

import java.util.function.Predicate;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.flapdoodle.solid.parser.content.Blob;
import de.flapdoodle.solid.site.Filters.Filter;

public interface FilterFactory {
	Predicate<Blob> filters(ImmutableSet<String> filters, ImmutableMap<String, Filter> filtersMap);
	
	public static FilterFactory defaultFilterFactory() {
		return new DefaultFilterFactory();
	}
}
