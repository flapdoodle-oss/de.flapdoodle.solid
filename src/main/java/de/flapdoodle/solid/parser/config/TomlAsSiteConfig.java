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
import java.util.Map;
import java.util.Optional;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.moandjiezana.toml.Toml;

import de.flapdoodle.solid.exceptions.SomethingWentWrong;
import de.flapdoodle.solid.io.Filenames;
import de.flapdoodle.solid.io.In;
import de.flapdoodle.solid.site.SiteConfig;
import de.flapdoodle.types.Either;
import de.flapdoodle.types.Try;

@Deprecated
public class TomlAsSiteConfig implements PathAsSiteConfig {

	private static final String SOLID_CONFIG = "solid.toml";

	@Override
	public Either<SiteConfig, FilenamePatterns> parse(Path path) {
		String filename = Filenames.filenameOf(path);
		
		if (filename.equals(SOLID_CONFIG)) {
			String content = Try.function(In::read)
					.mapCheckedException(SomethingWentWrong::new)
					.apply(path);
			
			return asSiteConfig(filename, new Toml().read(content));
		}
		return Either.right(FilenamePatterns.of(SOLID_CONFIG));
	}

	private static Either<SiteConfig, FilenamePatterns> asSiteConfig(String filename, Toml config) {
		System.out.println(config);
		println(config.toMap());
		
		return Either.left(SiteConfig.builder()
				.filename(filename)
				.baseUrl(config.getString("baseURL"))
				.theme(Optional.ofNullable(config.getString("theme", null)))
				.putAllProperties(commonPropertiesOf(config))
				.build());
	}

	private static void println(Map<String, Object> map) {
		println(map,0);
	}
	
	private static void println(Map<String, Object> map, int level) {
		String prefix=Strings.repeat("  ", level);
		
		map.forEach((key, val) -> {
			if (val instanceof Map) {
				System.out.println(prefix+key+"= {");
				println((Map) val,level+1);
				System.out.println(prefix+"}");
			} else {
				System.out.println(prefix+key+"="+val);
			}
		});
	}

	private static Map<String, ? extends String> commonPropertiesOf(Toml config) {
		Builder<String, String> builder = ImmutableMap.builder();
		putIfNotNull("title", config, builder);
		putIfNotNull("subtitle", config, builder);
		return builder.build();
	}
	
	private static void putIfNotNull(String key, Toml config, Builder<String, String> builder) {
		String value=config.getString(key);
		if (value!=null) {
			builder.put(key, value);
		}
	}

}
