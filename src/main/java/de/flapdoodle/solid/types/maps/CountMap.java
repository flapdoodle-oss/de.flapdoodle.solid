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
