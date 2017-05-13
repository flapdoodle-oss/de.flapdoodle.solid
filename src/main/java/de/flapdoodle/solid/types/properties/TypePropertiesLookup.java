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
package de.flapdoodle.solid.types.properties;

import java.util.Date;
import java.util.Optional;

public interface TypePropertiesLookup {
	<T> Optional<TypeProperties<T>> propertiesOf(Class<T> type);
	
	default TypePropertiesLookup withFallback(TypePropertiesLookup fallback) {
		TypePropertiesLookup that=this;
		
		return new TypePropertiesLookup() {
			
			@Override
			public <T> Optional<TypeProperties<T>> propertiesOf(Class<T> type) {
				Optional<TypeProperties<T>> ret = that.propertiesOf(type);
				if (!ret.isPresent()) {
					return fallback.propertiesOf(type);
				}
				return ret;
			}
		};
	}
	
	public static TypePropertiesLookup defaultLookup() {
		return MapBasedTypePropertiesLookup.builder()
				.add(Date.class, TypeProperties.dateProperties())
				.build();
	}
}
