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
package de.flapdoodle.solid.types;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;

public class Multimaps {

	public static <K,V> ImmutableList<ImmutableMap<K, V>> flatten(ImmutableMultimap<K, V> src) {
		ImmutableList.Builder<ImmutableMap<K, V>> listBuilder=ImmutableList.builder();
		
		if (!src.isEmpty()) {
			ImmutableMap<K, Collection<V>> map = src.asMap();
			int entries=map.values().stream().reduce(1, (s,l) -> s*l.size(), (a,b) -> a*b);
			
			ImmutableList<Line<K,V>> lines = map.entrySet().stream()
					.map(e -> new Line<>(e.getKey(), e.getValue()))
					.collect(ImmutableList.toImmutableList());
			
			for (int i=0;i<entries;i++) {
				ImmutableMap.Builder<K, V> mapBuilder = ImmutableMap.builder();
				
				int fact=1;
				for (Line<K,V> line: lines) {
					mapBuilder.put(line.key, line.get((i/fact) % line.values.length));
					fact=fact*line.values.length;
				}
				
				listBuilder.add(mapBuilder.build());
			}
		}
		return listBuilder.build();
	}

	static class Factor {
		int current=1;
	}
	
	static class Line<K,V> {
		private final K key;
		private final Object[] values;

		public Line(K key, Collection<V> values) {
			this.key = key;
			this.values = values.toArray();
		}
		
		public V get(int idx) {
			return (V) values[idx];
		}
	}
	
	public static <K,V> ImmutableMap<K, V> reverseOrdering(Map<K, V> src) {
		ImmutableMap.Builder<K, V> builder=ImmutableMap.builder();
		
		ImmutableList.copyOf(src.entrySet()).reverse().forEach(e -> {
			builder.put(e.getKey(), e.getValue());
		});
		
		return builder.build();
	}
	
}
