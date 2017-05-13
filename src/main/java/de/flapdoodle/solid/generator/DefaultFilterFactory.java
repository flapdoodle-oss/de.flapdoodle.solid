package de.flapdoodle.solid.generator;

import java.util.function.Predicate;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.flapdoodle.solid.parser.content.Blob;
import de.flapdoodle.solid.site.Filters.Filter;

public class DefaultFilterFactory implements FilterFactory {

	@Override
	public Predicate<Blob> filters(ImmutableSet<String> filters, ImmutableMap<String, Filter> filtersMap) {
		return matchAll(filters.stream()
			.map(f -> filtersMap.get(f))
			.filter(f -> f != null)
			.map(DefaultFilterFactory::asPredicate)
			.collect(ImmutableList.toImmutableList()));
	}
	
	private static Predicate<Blob> asPredicate(Filter filter) {
		return blob -> {
			ImmutableList<Object> values = blob.meta().findList(Object.class, Splitter.on('.').split(filter.property()));
			switch (filter.operator()) {
				case Equals:
					return equals(filter.values(), values);
				case NotEquals:
					return !equals(filter.values(), values);
			}
			return false;
		};
	}
	
	private static boolean equals(ImmutableList<Object> expected, ImmutableList<Object> current) {
//		System.out.println("eq: "+expected+" ? "+current);
		return expected.equals(current);
	}

	private static <T> Predicate<T> matchAll(Iterable<Predicate<T>> predicates) {
		return t -> {
			for (Predicate <T> p  : predicates) {
				if (!p.test(t)) {
					return false;
				}
			}
			return true;
		};
	}

}
