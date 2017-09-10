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
package de.flapdoodle.solid.types.maps;

import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;

import de.flapdoodle.checks.Preconditions;

public abstract class CountMap {

	public static <K> ImmutableMap<K, Integer> scale(Map<K, Integer> map, int lowerBound, int upperBound) {
		Preconditions.checkArgument(upperBound>lowerBound, "upperBound(%s) <= lowerBound(%s)", upperBound,lowerBound);
		Optional<Integer> minValue = map.values().stream().min(Integer::compare);
		Optional<Integer> maxValue = map.values().stream().max(Integer::compare);
		if (minValue.isPresent() && maxValue.isPresent()) {
			int min=minValue.get();
			int max=maxValue.get();
			if (min<max) {
				int sourceScale=maxValue.get()-min;
				int destScale=upperBound-lowerBound;

				ImmutableMap.Builder<K, Integer> builder=ImmutableMap.builder();
				map.forEach((k, value) -> {
					int scaled=(((value-min)*destScale)/sourceScale)+lowerBound;
					builder.put(k,scaled);
				});
				return builder.build();
			}
		}
		return ImmutableMap.of();
	}
}
