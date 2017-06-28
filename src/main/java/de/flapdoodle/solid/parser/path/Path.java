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

import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Check;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import de.flapdoodle.solid.parser.path.ImmutablePath.Builder;
import de.flapdoodle.solid.parser.regex.Patterns;
import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.solid.types.Pair;

// /foo/bar/:property-nix/:other/nix
@Immutable
public abstract class Path {
	
	private static final Pattern PATH_PROP_PATTERN=Pattern.compile("(:((?<name>([a-zA-Z0-9]+))(#(?<formatter>[a-zA-Z0-9]+))?))");
	public static final String PAGE = "page";
	
	public abstract ImmutableList<Part> parts();
	
	@Check
	protected void check() {
		ImmutableList<String> names = propertyNames();
		ImmutableSet<String> asSet = ImmutableSet.copyOf(names);
		Preconditions.checkArgument(names.size()==asSet.size(),"property used more than once: %s",names);
		int indexOfPageProperty = names.indexOf(PAGE);
		Preconditions.checkArgument(indexOfPageProperty==-1 || indexOfPageProperty==(names.size()-1),"page property is not last in path: %s",names);
	}
	
	@Auxiliary
	public boolean isEmpty() {
		return parts().isEmpty();
	}
	
	@Auxiliary
	public boolean isPagedEmpty() {
		return parts().isEmpty() || propertyNamesWithoutPage().isEmpty();
	}
	
	public ImmutableList<String> propertyNamesWithoutPage() {
		return propertyNames().stream()
				.filter(n -> !n.equals(PAGE))
				.collect(ImmutableList.toImmutableList());
	}
		
	@Auxiliary
	public ImmutableList<String> propertyNames() {
		return parts().stream()
			.filter(p -> p instanceof Property)
			.map(p -> ((Property) p).property())
			.collect(ImmutableList.toImmutableList());
	}
	
	@Auxiliary
	public Pair<Path, Path> split(Predicate<Part> matcher) {
		int idx=Iterables.indexOf(parts(), p -> matcher.test(p));
		if (idx!=-1) {
			return Pair.of(Path.of(parts().subList(0, idx+1)), Path.of(parts().subList(idx+1, parts().size())));
		}
		return Pair.of(this, Path.emtpy());
	}
	
	public interface Part {
		
	}
	
	@Immutable
	public interface Static extends Part {
		@Parameter
		String fixed();
	}
	
	@Immutable
	public interface Property extends Part {
		@Parameter
		String property();
		
		@Parameter
		Maybe<String> formatter();
	}
	
	public static Path parse(String src) {
		Builder builder = ImmutablePath.builder();
		Patterns.parse(PATH_PROP_PATTERN, src, either -> {
			if (either.isLeft()) {
				builder.addParts(ImmutableStatic.of(either.left()));
			} else {
				builder.addParts(ImmutableProperty.of(either.right().group("name"),Maybe.ofNullable(either.right().group("formatter"))));
			}
		});
		return builder.build();
	}

	private static Path emtpy() {
		return ImmutablePath.builder().build();
	}
	
	private static Path of(Iterable<? extends Part> parts) {
		return ImmutablePath.builder()
				.addAllParts(parts)
				.build();
	}
}
