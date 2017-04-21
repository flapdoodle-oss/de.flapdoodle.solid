package de.flapdoodle.solid.types;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public interface GroupedPropertyMap {

	Optional<Object> get(String ... key);

	ImmutableMap<String, Object> propertiesOf(String ... group);

	ImmutableSet<String> groupsOf(String ... group);

	public static ImmutableGroupedPropertyMap.Builder builder() {
		return ImmutableGroupedPropertyMap.builder();
	}
}
