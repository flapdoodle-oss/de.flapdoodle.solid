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
package de.flapdoodle.solid.parser.path;

import java.util.regex.Pattern;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.parser.path.ImmutablePath.Builder;
import de.flapdoodle.solid.parser.regex.Patterns;

// /foo/bar/:property-nix/:other/nix
@Immutable
public abstract class Path {
	
	private static final Pattern PATH_PROP_PATTERN=Pattern.compile("(:(?<name>[a-zA-Z0-9]+))");
	
	public abstract ImmutableList<Part> parts();
	
	interface Part {
		
	}
	
	@Immutable
	interface Static extends Part {
		@Parameter
		String fixed();
	}
	
	@Immutable
	interface Property extends Part {
		@Parameter
		String property();
	}
	
	public static Path parse(String src) {
		Builder builder = ImmutablePath.builder();
		Patterns.parse(PATH_PROP_PATTERN, src, either -> {
			if (either.isLeft()) {
				builder.addParts(ImmutableStatic.of(either.left()));
			} else {
				builder.addParts(ImmutableProperty.of(either.right().group("name")));
			}
		});
		return builder.build();
	}
}
