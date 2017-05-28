package de.flapdoodle.solid.theme.mustache;

import java.io.FileReader;
import java.nio.file.Path;

import com.google.common.collect.ImmutableMap;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Mustache.Collector;
import com.samskivert.mustache.Mustache.Compiler;
import com.samskivert.mustache.Mustache.Formatter;
import com.samskivert.mustache.Mustache.TemplateLoader;
import com.samskivert.mustache.Template;

import de.flapdoodle.solid.content.render.MarkupRendererFactory;
import de.flapdoodle.solid.exceptions.SomethingWentWrong;
import de.flapdoodle.solid.generator.Text;
import de.flapdoodle.solid.theme.AbstractTheme;
import de.flapdoodle.solid.theme.Renderer;
import de.flapdoodle.solid.types.tree.PropertyTree;
import de.flapdoodle.types.Try;

public class MustacheTheme extends AbstractTheme {

	private final Compiler compiler;
	final ThreadLocal<ImmutableMap<String, de.flapdoodle.solid.formatter.Formatter>> formatter=new ThreadLocal<>();

	public MustacheTheme(Path rootDir, PropertyTree config, MarkupRendererFactory markupRenderFactory) {
		super(rootDir, config, markupRenderFactory);
		this.compiler = Mustache.compiler()
				.withLoader(loaderOf(this.rootDir))
				.defaultValue("")
				.withCollector(customCollector())
				.withFormatter(customFormatter())
//				.withEscaper(Escapers.NONE)
				.emptyStringIsFalse(true);
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
				ImmutableMustacheRenderContext renderContext = MustacheRenderContext.builder()
						.markupRenderFactory(markupRenderFactory)
						.build();
				
				return Text.builder()
						.mimeType("text/html")
						.text(template.execute(asMustacheContext(renderable, renderContext)))
						.build();
			} catch (RuntimeException rx) {
				throw new RuntimeException("could not render: "+templateName,rx);
			}
		};
	}

	protected Object asMustacheContext(Renderer.Renderable renderable, MustacheRenderContext renderContext) {
		return MustacheWrapper.builder()
			.renderContext(renderContext)
			.context(renderable.context())
			.addAllAllBlobs(renderable.blobs())
			.build();
	}
}
