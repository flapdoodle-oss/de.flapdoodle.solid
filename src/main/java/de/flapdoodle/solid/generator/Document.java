/*
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

import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Function;

import org.immutables.value.Value.Immutable;

import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.io.In;
import de.flapdoodle.solid.types.Maybe;

@Immutable
public interface Document {
	String path();
	
	Content content();
	
	public static ImmutableDocument.Builder builder() {
		return ImmutableDocument.builder();
	}
	
	public static ImmutableList<Document> of(Path rootDir, Function<Path, String> pathMapping) throws IOException {
		return In.walk(rootDir, (path,content) -> {
			return Maybe.of((Document) Document.builder()
					.path(pathMapping.apply(rootDir.relativize(path)))
					.content(Binary.builder()
							.mimeType(In.mimeTypeOf(path))
							.data(content)
					.build())
					.build());
			});
	}
}
