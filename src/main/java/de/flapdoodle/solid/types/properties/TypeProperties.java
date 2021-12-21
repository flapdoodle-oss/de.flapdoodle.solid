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
package de.flapdoodle.solid.types.properties;

import java.util.Date;

import org.immutables.value.Value.Immutable;

import com.google.common.collect.ImmutableMap;

import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.solid.types.dates.Dates;

@Immutable
public interface TypeProperties<T> {
	ImmutableMap<String, TypeProperty<T, ?>> map();
	
	default Maybe<TypeProperty<T, ?>> of(String name) {
		return Maybe.ofNullable(map().get(name));
	}
	
	public static <T> ImmutableTypeProperties.Builder<T> builder(Class<T> type) {
		return ImmutableTypeProperties.builder();
	}
	
	public static TypeProperties<Date> dateProperties() {
		return TypeProperties.builder(Date.class)
				.putMap("year", date -> Dates.map(date).getYear())
				.putMap("month", date -> Dates.map(date).getMonthValue())
				.putMap("day", date -> Dates.map(date).getDayOfMonth())
				.build();
	}
}
