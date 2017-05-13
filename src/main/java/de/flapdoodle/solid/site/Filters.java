package de.flapdoodle.solid.site;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.flapdoodle.legacy.Optionals;
import de.flapdoodle.solid.types.tree.PropertyTree;

@Immutable
public interface Filters {

	ImmutableMap<String, Filter> filters();
	
	enum Operator {
		Equals,NotEquals;
		
		public static Operator of(String name) {
			switch (name) {
				case "eq":
					return Equals;
				case "!eq":
					return NotEquals;
			}
			
			return Preconditions.checkNotNull(null,"could not get operator for %s",name);
		}
	}
	
	@Immutable
	interface Filter {
		@Parameter
		String property();
		@Parameter
		Operator operator();
		@Parameter
		ImmutableList<Object> values();
	}
	
	public static Filters of(PropertyTree filter) {
		ImmutableFilters.Builder builder = ImmutableFilters.builder();
		filter.properties().forEach(name -> {
			PropertyTree filterConfig = Optionals.checkPresent(filter.find(name),"not a valid regex config: %s",name).get();
			String property = Optionals.checkPresent(filterConfig.find(String.class, "property"),"property not set for %s",name).get();
			String operator = Optionals.checkPresent(filterConfig.find(String.class, "operator"),"operator not set for %s",name).get();
			Object value = Optionals.checkPresent(filterConfig.find(Object.class, "value"),"value not set for %s",name).get();
			
			builder.putFilters(name, ImmutableFilter.builder()
				.property(property)
				.operator(Operator.of(operator))
				.addAllValues(ImmutableList.of(value))
				.build());
		});
		return builder.build();
	}

	public static Filters empty() {
		return ImmutableFilters.builder()
				.build();
	}

	
}
