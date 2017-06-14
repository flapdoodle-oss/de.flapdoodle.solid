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
			.onCheckedException(ex -> ImmutableList.of())
			.get();
	}

	@Override
	public ImmutableList<Document> staticFiles() {
		return staticFiles;
	}

}
