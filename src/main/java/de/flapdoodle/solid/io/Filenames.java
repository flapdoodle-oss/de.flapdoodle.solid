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

import java.nio.file.Path;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class Filenames {
	
	public static String filenameOf(Path path) {
		return path.getFileName().toString();
	}
	
	public static String extensionOf(Path path) {
		return extensionOf(filenameOf(path));
	}

	public static String extensionOf(String filename) {
		int lastIndex = filename.lastIndexOf(".");
		return lastIndex != -1 
				? filename.substring(lastIndex+1)
				: "";
	}

	public static ImmutableList<String> pathAsList(Path path) {
		Builder<String> builder = ImmutableList.builder();
		path.forEach(p -> {
			builder.add(filenameOf(p));
		});
		return builder.build();
	}
}
