/*
 * Copyright (C) 2017
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
