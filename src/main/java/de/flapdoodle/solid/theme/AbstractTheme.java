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
package de.flapdoodle.solid.theme;

import java.nio.file.Path;

import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.content.render.MarkupRendererFactory;
import de.flapdoodle.solid.generator.Document;
import de.flapdoodle.solid.types.tree.PropertyTree;
import de.flapdoodle.types.Try;

public abstract class AbstractTheme implements Theme {

	protected final Path rootDir;
	protected final PropertyTree config;
	protected final MarkupRendererFactory markupRenderFactory;
	protected final ImmutableList<Document> staticFiles;

	public AbstractTheme(Path rootDir, PropertyTree config, MarkupRendererFactory markupRenderFactory) {
		this.rootDir = rootDir;
		this.config = config;
		this.markupRenderFactory = markupRenderFactory;
		this.staticFiles = staticFilesOf(rootDir);
	}
	
	private static ImmutableList<Document> staticFilesOf(Path rootDir) {
		return Try.supplier(() -> {
			Path staticContentPath = rootDir.resolve("static");
			return Document.of(staticContentPath, path -> path.toString());
		})
			.fallbackTo(ex -> ImmutableList.of())
			.get();
	}

	@Override
	public ImmutableList<Document> staticFiles() {
		return staticFiles;
	}

}
