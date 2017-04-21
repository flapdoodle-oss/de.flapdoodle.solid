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

import java.util.Map;
import java.util.Optional;

import org.immutables.value.Value;
import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Parameter;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public final class ImmutableGroupedPropertyMap implements GroupedPropertyMap {

	private final ImmutableMap<Key, ImmutableMap<String, Object>> mapOfMaps;
	private final ImmutableMultimap<Key, String> groupsOfKey;
	
	public ImmutableGroupedPropertyMap(ImmutableMap<Key, ImmutableMap<String, Object>> mapOfMaps, ImmutableMultimap<Key, String> groupsOfKey) {
		this.mapOfMaps = mapOfMaps;
		this.groupsOfKey = groupsOfKey;
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass()).addValue(mapOfMaps).toString();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((groupsOfKey == null) ? 0 : groupsOfKey.hashCode());
		result = prime * result + ((mapOfMaps == null) ? 0 : mapOfMaps.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImmutableGroupedPropertyMap other = (ImmutableGroupedPropertyMap) obj;
		if (groupsOfKey == null) {
			if (other.groupsOfKey != null)
				return false;
		} else if (!groupsOfKey.equals(other.groupsOfKey))
			return false;
		if (mapOfMaps == null) {
			if (other.mapOfMaps != null)
				return false;
		} else if (!mapOfMaps.equals(other.mapOfMaps))
			return false;
		return true;
	}



	@Override
	public Optional<Object> find(String ... key) {
		Key mapKey = Key.of(key);
		ImmutableMap<String, Object> map = mapOfMaps.get(mapKey.parent());
		if (map!=null) {
			return Optional.ofNullable(map.get(mapKey.last()));
		}
		return Optional.empty();
	}
	
	@Override
	public ImmutableMap<String, Object> propertiesOf(String ... group) {
		ImmutableMap<String, Object> map = mapOfMaps.get(Key.of(group));
		if (map!=null) {
			return map;
		}
		return ImmutableMap.of();
	}
	
	@Override
	public ImmutableSet<String> groupsOf(String ... group) {
		return ImmutableSet.copyOf(groupsOfKey.get(Key.of(group)));
	}

	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		Map<Key, Map<String, Object>> maps=Maps.newLinkedHashMap();
		
		public Builder put(String key, Object value) {
			return putValue(value, key);
		}
		
		public Builder put(String key, String key2, Object value) {
			return putValue(value, key, key2);
		}
		
		public Builder put(String key, String key2, String key3, Object value) {
			return putValue(value, key, key2, key3);
		}

		public Builder putValue(Object value, String ...key) {
			return putIntern(Key.of(key), value);
		}
		
		public Builder put(Iterable<String> key, Object value) {
			return putIntern(Key.of(key), value);
		}
		
		private Builder putIntern(Key key, Object value) {
			Key parent = key.parent();
			if (!parent.isRoot()) {
				Preconditions.checkArgument(!mapOfGroup(parent.parent()).containsKey(parent.last()),"there is already a value set to %s",parent);
			}
			Map<String, Object> propertyMap = mapOfGroup(parent);
			Object old = propertyMap.put(key.last(), value);
			Preconditions.checkArgument(old==null,"value for %s already set to %s",key, old);
			return this;
		}

		private Map<String, Object> mapOfGroup(Key key) {
			Map<String, Object> ret = maps.get(key);
			if (ret==null) {
				ret = Maps.newLinkedHashMap();
				maps.put(key, ret);
			}
			return ret;
		}
		
		public ImmutableGroupedPropertyMap build() {
			ImmutableMap.Builder<Key, ImmutableMap<String, Object>> mapOfMapsBuilder=ImmutableMap.builder();
			ImmutableMultimap.Builder<Key, String> groupOfKeyBuilder=ImmutableMultimap.builder();
			
			maps.forEach((key, map) -> {
				mapOfMapsBuilder.put(key, ImmutableMap.copyOf(map));
			});
			
			maps.keySet().forEach(key -> {
				Key current=key;
				while (!current.isRoot()) {
					Key parent = current.parent();
					groupOfKeyBuilder.put(parent, current.last());
					current=parent;
				}
			});
			
			return new ImmutableGroupedPropertyMap(mapOfMapsBuilder.build(), groupOfKeyBuilder.build());
		}
	}
	
	@Value.Immutable
	static abstract class Key {
		@Parameter
		protected abstract ImmutableList<String> path();
		
		protected Key parent() {
			Preconditions.checkArgument(!path().isEmpty(),"this key has no parent: %s", this);
			return ImmutableKey.of(path().subList(0, path().size()-1));
		}
		
		@Auxiliary
		protected boolean isRoot()  {
			return path().isEmpty();
		}
		
		protected String last() {
			Preconditions.checkArgument(!path().isEmpty(),"this key has no last element: %s", this);
			return path().get(path().size()-1);
		}

		public static Key of(String...args) {
			return ImmutableKey.of(ImmutableList.copyOf(args));
		}
		
		public static Key of(Iterable<String> args) {
			return ImmutableKey.of(args);
		}
	}
}
