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
package de.flapdoodle.solid.generator;

import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;

import de.flapdoodle.solid.parser.content.Blob;
import de.flapdoodle.solid.parser.path.Path;

@Immutable
public interface GroupedBlobs {
	
	Path currentPath();
	ImmutableMultimap<ImmutableMap<String, Object>, Blob> groupedBlobs();
	
	@Auxiliary
	default ImmutableCollection<ImmutableMap<String,Object>> keysOf(Blob blob) {
		return groupedBlobs().inverse().get(blob);
	}
	
	public static ImmutableGroupedBlobs.Builder builder() {
		return ImmutableGroupedBlobs.builder();
	}
}