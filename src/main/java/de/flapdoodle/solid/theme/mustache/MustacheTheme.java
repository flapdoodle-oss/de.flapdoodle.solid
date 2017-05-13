package de.flapdoodle.solid.theme.mustache;

import java.io.FileReader;
import java.nio.file.Path;
import java.util.List;

import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;

import com.google.common.collect.ImmutableList;
import com.samskivert.mustache.DefaultCollector;
import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Mustache.Collector;
import com.samskivert.mustache.Mustache.Compiler;
import com.samskivert.mustache.Mustache.Formatter;
import com.samskivert.mustache.Mustache.TemplateLoader;
import com.samskivert.mustache.Mustache.VariableFetcher;
import com.samskivert.mustache.Template;

import de.flapdoodle.solid.exceptions.SomethingWentWrong;
import de.flapdoodle.solid.generator.Text;
import de.flapdoodle.solid.parser.content.Blob;
import de.flapdoodle.solid.theme.Renderer;
import de.flapdoodle.solid.theme.Renderer.Context;
import de.flapdoodle.solid.theme.Theme;
import de.flapdoodle.solid.types.tree.PropertyTree;
import de.flapdoodle.types.Either;
import de.flapdoodle.types.Try;

public class MustacheTheme implements Theme {

	private final Path rootDir;
	private final Compiler compiler;

	public MustacheTheme(Path rootDir) {
		this.rootDir = rootDir;
		this.compiler = Mustache.compiler()
				.withLoader(loaderOf(this.rootDir))
				.defaultValue("")
				.withCollector(customCollector())
				.withFormatter(customFormatter())
				.emptyStringIsFalse(true);
	}

	private static Formatter customFormatter() {
		return new Formatter() {
			@Override
			public String format(Object value) {
				if (value instanceof List) {
					List l = (List) value;
					if (l.size()==1) {
						return format(l.get(0));
					}
				}
				if (value instanceof Either) {
					Either either = (Either) value;
					return format(either.isLeft() ? either.left() : either.right());
				}
				return value.toString();
			}
		};
	}

	private static Collector customCollector() {
		return new DefaultCollector() {
			
			@Override
			public VariableFetcher createFetcher(Object ctx, String name) {
				VariableFetcher ret = super.createFetcher(ctx, name);
				if (ret==null) {
					if (ctx instanceof PropertyTree) {
						return (c,n) -> ((PropertyTree) c).get(n);
					}
				}
				return ret;
			}
		};
	}

	private static TemplateLoader loaderOf(Path root) {
		return name -> new FileReader(root.resolve(name+".mustache").toFile());
	}

	@Override
	public Renderer rendererFor(String templateName) {
		Template template = Try.supplier(() -> compiler.compile(new FileReader(rootDir.resolve(templateName+".mustache").toFile())))
			.mapCheckedException(SomethingWentWrong::new)
			.get();
		
		return rendererOf(template);
	}

	private Renderer rendererOf(Template template) {
		return renderable -> Text.builder()
				.mimeType("text/html")
				.text(template.execute(asMustacheContext(renderable)))
				.build();
	}

	protected Object asMustacheContext(Renderer.Renderable renderable) {
		return MustacheWrapper.builder()
			.context(renderable.context())
			.addAllBlobs(renderable.blobs())
			.build();
	}
	
	@Immutable
	interface MustacheWrapper {
		ImmutableList<Blob> blobs();
		Context context();
		
		@Auxiliary
		default Blob getSingle() {
			return blobs().size()==1 ? blobs().get(0) : null;
		}
		
		public static ImmutableMustacheWrapper.Builder builder() {
			return ImmutableMustacheWrapper.builder();
		}
	}
}
