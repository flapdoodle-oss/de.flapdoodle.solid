package de.flapdoodle.solid.types;

import java.util.Collection;

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
}
