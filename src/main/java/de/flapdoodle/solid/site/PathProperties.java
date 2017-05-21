/**
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

import java.util.List;

import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;

import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.solid.types.tree.PropertyTree;
import de.flapdoodle.types.Either;

@Immutable
public interface PathProperties {
	ImmutableMultimap<String, String> alias();
	
	@Auxiliary
	default ImmutableCollection<String> mapped(String src) {
		return Maybe.ofNullable(alias().get(src)).orElse(() -> ImmutableList.of(src));
	}
	
	@Auxiliary
	default PathProperties merge(PathProperties overRide) {
		ImmutablePathProperties.Builder builder = ImmutablePathProperties.builder();
		builder.putAllAlias(overRide.alias());
		this.alias().forEach((key,value) -> {
			if (!overRide.alias().containsKey(key)) {
				builder.putAlias(key, value);
			}
		});
		return builder.build();
	}
	
	public static PathProperties of(PropertyTree map) {
		ImmutablePathProperties.Builder builder = ImmutablePathProperties.builder();
		map.properties().forEach(key -> {
			List<Either<Object, ? extends PropertyTree>> values = map.get(key);
			Preconditions.checkArgument(values.stream().filter(e -> !e.isLeft() || !(e.left() instanceof String)).count()==0,"invalid mapping for %s (%s)",key,values);
			ImmutableList<String> all = values.stream().map(e -> (String) e.left()).collect(ImmutableList.toImmutableList());
			builder.putAllAlias(key, all);
		});
		return builder.build();
	}
	
	public static PathProperties empty() {
		return ImmutablePathProperties.builder().build();
	}
	
	public static PathProperties defaults() {
		return ImmutablePathProperties.builder()
			.putAlias("year", "date.year")
			.putAlias("month", "date.month")
			.putAlias("day", "date.day")
			.build();
	}
}
