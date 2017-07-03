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
package de.flapdoodle.solid.site;

import java.util.function.Function;
import java.util.regex.Pattern;

import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Lazy;

import com.google.common.collect.ImmutableList;

import de.flapdoodle.legacy.Optionals;
import de.flapdoodle.solid.site.ImmutableUrlRewrite.Builder;
import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.solid.types.tree.PropertyTree;

@Immutable
public interface UrlRewrite {
	
	ImmutableList<UrlRegex> regex();
	
	@Lazy
	default Function<String, Maybe<String>> rewriter() {
		return new Rewriter(regex());
	}
	
	@Immutable
	interface UrlRegex {
		String name();
		Pattern pattern();
		String replacement();
		
		@Auxiliary
		default String rewrite(String source) {
			return pattern().matcher(source).replaceAll(replacement());
		}
		
		@Auxiliary
		default boolean matches(String source) {
			return pattern().matcher(source).matches();
		}
		
		public static ImmutableUrlRegex.Builder builder() {
			return ImmutableUrlRegex.builder();
		}
	}
	
	public static UrlRewrite of(PropertyTree tree) {
		ImmutableUrlRewrite.Builder builder = builder();
		tree.find("regex").ifPresent(regex -> {
			regex.properties().forEach(name -> {
				PropertyTree regexConfig = Optionals.checkPresent(regex.find(name),"not a valid regex config: %s",name).get();
				String pattern = Optionals.checkPresent(regexConfig.find(String.class, "pattern"),"pattern not set for %s",name).get();
				String replacement = Optionals.checkPresent(regexConfig.find(String.class, "replacement"),"replacement not set for %s",name).get();
				
				builder.addRegex(UrlRegex.builder()
					.name(name)
					.pattern(Pattern.compile(pattern))
					.replacement(replacement)
					.build());
			});
		});
		return builder.build();
	}

	public static UrlRewrite empty() {
		return builder().build();
	}

	public static Builder builder() {
		return ImmutableUrlRewrite.builder();
	}
	
	public static class Rewriter implements Function<String, Maybe<String>> {

		private final ImmutableList<UrlRegex> regex;

		public Rewriter(ImmutableList<UrlRegex> regex) {
			this.regex = regex;
		}

		@Override
		public Maybe<String> apply(String source) {
			for (UrlRegex r : regex) {
				if (r.matches(source)) {
					return Maybe.of(r.rewrite(source));
				}
			}
			return Maybe.absent();
		}
		
	}
}
