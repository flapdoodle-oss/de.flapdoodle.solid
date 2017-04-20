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
package de.flapdoodle.solid.parser.config;

import java.nio.file.Path;
import java.util.function.Function;

import de.flapdoodle.solid.parser.config.ImmutableFilenamePatterns.Builder;
import de.flapdoodle.solid.site.SiteConfig;
import de.flapdoodle.types.Either;

public interface PathAsSiteConfig {

	Either<SiteConfig, FilenamePatterns> parse(Path path);
	
	default Function<Path, Either<SiteConfig, FilenamePatterns>> asFunction() {
		return path -> this.parse(path);
	}
	
	public static PathAsSiteConfig composite(PathAsSiteConfig ... list) {
		return path -> {
			Builder missedPatternsBuilder = FilenamePatterns.builder();
			for (PathAsSiteConfig l : list) {
				Either<SiteConfig, FilenamePatterns> config = l.parse(path);
				if (config.isLeft()) {
					return config;
				} else {
					missedPatternsBuilder.addAllPatterns(config.right().patterns());
				}
			}
			return Either.right(missedPatternsBuilder.build());
		};
	}
	
	public static PathAsSiteConfig defaults() {
		return composite(new TomlAsSiteConfig());
	}
}
