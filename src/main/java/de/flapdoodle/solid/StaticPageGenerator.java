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
package de.flapdoodle.solid;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import de.flapdoodle.solid.generator.SiteGenerator;
import de.flapdoodle.solid.io.PathWatcher;
import de.flapdoodle.solid.parser.content.Site;
import de.flapdoodle.solid.threads.Deferer;

public interface StaticPageGenerator {
	Runnable generator(SiteSpring siteSpring, SiteGenerator generator, PageSink pageSink);
	
	public static StaticPageGenerator once() {
		return (siteSpring, generator, pageSink) -> () -> {
			Site site = siteSpring.get();
			pageSink.accept(site.config(), generator.generate(site));
		};
	}
	
	public static StaticPageGenerator onChange(Path dir, Path ... excludes) {
		ImmutableSet<Path> excludesAsSet=ImmutableSet.copyOf(excludes);
		
		return (siteSpring, generator, pageSink) -> () -> {
			try {
				Site site = siteSpring.get();
				pageSink.accept(site.config(), generator.generate(site));
			} catch (RuntimeException rx) {
				rx.printStackTrace();
			}
			
			Consumer<Multimap<String, Path>> generateOnChange = Deferer.call((Multimap<String, Path> changes) -> {
				try {
					Site site = siteSpring.get();
					pageSink.accept(site.config(), generator.generate(site));
				} catch (RuntimeException rx) {
					rx.printStackTrace();
				}
			})
				.onInactivityFor(1, TimeUnit.SECONDS);
			
			PathWatcher.watch(dir)
				.every(5, TimeUnit.SECONDS)
				.with((Multimap<String, Path> changes) -> {
					Multimap<String, Path> withoutExcludedPathEntries = Multimaps.filterValues(changes, path -> !excludesAsSet.stream()
							.filter(e -> isParentOf(e,path))
							.findAny().isPresent());
					
					if (!withoutExcludedPathEntries.isEmpty()) {
						System.out.println("Something has changed: "+withoutExcludedPathEntries);
						generateOnChange.accept(withoutExcludedPathEntries);
					}
					return false;
				});
		};
	}

	static boolean isParentOf(Path exclude, Path path) {
		return path.toAbsolutePath().startsWith(exclude.toAbsolutePath());
	}
}
