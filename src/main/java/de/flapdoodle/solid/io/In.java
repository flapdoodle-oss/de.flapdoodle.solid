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
package de.flapdoodle.solid.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.exceptions.SomethingWentWrong;
import de.flapdoodle.solid.types.ByteArray;
import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.solid.types.Streams;
import de.flapdoodle.types.Try;

public abstract class In {

	public static String read(Path path) throws IOException {
		return new String(Files.readAllBytes(path),StandardCharsets.UTF_8);
	}
	
	public static <T> ImmutableList<T> walk(Path root,BiFunction<Path, ByteArray, Maybe<T>> mapper) throws IOException {
		ImmutableList.Builder<T> builder=ImmutableList.builder();
		
		walk(root, (relativePath, content) -> {
			mapper.apply(relativePath, content).ifPresent(builder::add);
		});
		
		return builder.build();
	}

	public static void walk(Path root,BiConsumer<Path, ByteArray> consumer) throws IOException {
		Streams.autocloseThrowing(() -> Files.walk(root))
			.forEach(path -> {
				if (path.toFile().isFile()) {
//					Path relativePath = root.relativize(path);
					ByteArray content = Try.supplier(() -> ByteArray.fromArray(Files.readAllBytes(path)))
						.mapCheckedException(SomethingWentWrong::new)
						.get();
					
					consumer.accept(path, content);
				}
			});
	}
	
	public static String mimeTypeOf(Path path) {
		return Try.supplier(() -> Files.probeContentType(path))
				.mapCheckedException(RuntimeException::new)
				.get();
	}
}
