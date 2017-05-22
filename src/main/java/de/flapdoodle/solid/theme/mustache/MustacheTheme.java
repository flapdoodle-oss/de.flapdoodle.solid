package de.flapdoodle.solid.theme.mustache;

import java.io.FileReader;
import java.nio.file.Path;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Mustache.Collector;
import com.samskivert.mustache.Mustache.Compiler;
import com.samskivert.mustache.Mustache.Formatter;
import com.samskivert.mustache.Mustache.TemplateLoader;
import com.samskivert.mustache.Template;

import de.flapdoodle.solid.exceptions.SomethingWentWrong;
import de.flapdoodle.solid.generator.Binary;
import de.flapdoodle.solid.generator.Document;
import de.flapdoodle.solid.generator.Text;
import de.flapdoodle.solid.io.In;
import de.flapdoodle.solid.theme.Renderer;
import de.flapdoodle.solid.theme.Theme;
import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.solid.types.tree.PropertyTree;
import de.flapdoodle.types.Try;

public class MustacheTheme implements Theme {

	private final Path rootDir;
	private final Compiler compiler;
	final ThreadLocal<ImmutableMap<String, de.flapdoodle.solid.formatter.Formatter>> formatter=new ThreadLocal<>();
	private final ImmutableList<Document> staticFiles;
	private final PropertyTree config;

	public MustacheTheme(Path rootDir, PropertyTree config) {
		this.rootDir = rootDir;
		this.config = config;
		this.compiler = Mustache.compiler()
				.withLoader(loaderOf(this.rootDir))
				.defaultValue("")
				.withCollector(customCollector())
				.withFormatter(customFormatter())
				.emptyStringIsFalse(true);
		this.staticFiles = staticFilesOf(rootDir);
	}
	
	private ImmutableList<Document> staticFilesOf(Path rootDir) {
		return Try.supplier(() -> {
			Path staticContentPath = rootDir.resolve("static");
			return In.walk(staticContentPath, (path,content) -> {
				return Maybe.of((Document) Document.builder()
						.path(staticContentPath.relativize(path).toString())
						.content(Binary.builder()
								.mimeType(In.mimeTypeOf(path))
								.data(content)
						.build())
						.build());
				});
		})
			.onCheckedException(ex -> ImmutableList.of())
			.get();
	}

	@Override
	public ImmutableList<Document> staticFiles() {
		return staticFiles;
	}

	private static Formatter customFormatter() {
		return new Formatter() {
			@Override
			public String format(Object value) {
				return CustomCollector.singleValue(value)
						.orElse(() -> value)
						.toString();
			}
		};
	}

	private Collector customCollector() {
		return new CustomCollector(this);
	}

	private static TemplateLoader loaderOf(Path root) {
		return name -> new FileReader(root.resolve(name.replace("\\", "/")+".mustache").toFile());
	}

	@Override
	public Renderer rendererFor(String templateName) {
		Template template = Try.supplier(() -> compiler.compile(new FileReader(rootDir.resolve(templateName+".mustache").toFile())))
			.mapCheckedException(SomethingWentWrong::new)
			.get();
		
		return rendererOf(template, templateName);
	}

	private Renderer rendererOf(Template template, String templateName) {
		return renderable -> {
			formatter.set(renderable.context().site().config().formatters().formatters());
			try {
				return Text.builder()
						.mimeType("text/html")
						.text(template.execute(asMustacheContext(renderable)))
						.build();
			} catch (RuntimeException rx) {
				throw new RuntimeException("could not render: "+templateName,rx);
			}
		};
	}

	protected Object asMustacheContext(Renderer.Renderable renderable) {
		return MustacheWrapper.builder()
			.context(renderable.context())
			.addAllBlobs(renderable.blobs())
			.build();
	}
}
