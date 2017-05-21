package de.flapdoodle.solid.theme.mustache;

import java.io.FileReader;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
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
import de.flapdoodle.solid.site.SiteConfig;
import de.flapdoodle.solid.theme.Renderer;
import de.flapdoodle.solid.theme.Renderer.Context;
import de.flapdoodle.solid.theme.Theme;
import de.flapdoodle.solid.types.reflection.Inspector;
import de.flapdoodle.solid.types.tree.PropertyTree;
import de.flapdoodle.types.Either;
import de.flapdoodle.types.Try;

public class MustacheTheme implements Theme {

	private final Path rootDir;
	private final Compiler compiler;
	private final ThreadLocal<ImmutableMap<String, de.flapdoodle.solid.formatter.Formatter>> formatter=new ThreadLocal<>();

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
				return singleValue(value)
						.orElse(value)
						.toString();
			}
		};
	}

	private Collector customCollector() {
		return new DefaultCollector() {
			
			@Override
			public VariableFetcher createFetcher(Object ctx, String name) {
				try {
//					Preconditions.checkArgument(!name.isEmpty(),"you should not use something like {{ .Foo }}");
					if (name.isEmpty()) {
						return (c,n) -> c;
					}
					
					VariableFetcher ret = super.createFetcher(ctx, name);
					if (ret==null) {
//						System.out.println(""+ctx.getClass()+".'"+name+"'");
						if ("*".equals(name)) {
							return (c,n) -> Inspector.propertyNamesOf(c.getClass());
						}
						if (ctx instanceof PropertyTree) {
							return (c,n) -> ((PropertyTree) c).get(n);
						}
						if (ctx instanceof MustacheFormating) {
							ImmutableMap<String, de.flapdoodle.solid.formatter.Formatter> map = Preconditions.checkNotNull(formatter.get(),"formatter map not set");
							de.flapdoodle.solid.formatter.Formatter formatter = map.get(name);
							return (c,n) ->formatter.format(((MustacheFormating) c).value()).orElse("");
						}
						if (name.equals("formatWith")) {
							return (c,n) -> singleValue(c).map(MustacheFormating::of).orElse(null);
						}
					}
					return ret;
				} catch (RuntimeException rx) {
					throw new RuntimeException("ctx.class: "+ctx.getClass()+", name: '"+name+"'",rx);
				}
			}
		};
	}

	private static Optional<Object> singleValue(Object c) {
		if (c instanceof List) {
			List l=(List) c;
			if (l.size()==1) {
				return singleValue(l.get(0));
			}
			return Optional.empty();
		}
		if (c instanceof Either) {
			Either e=(Either) c;
			return e.isLeft() ? singleValue(e.left()) : singleValue(e.right());
		}
		return Optional.ofNullable(c);
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
//		ImmutableMap<String, de.flapdoodle.solid.formatter.Formatter> formatters = renderable.context().site().config().formatters().formatters();
//		new Mustache.Lambda() {
//			
//			@Override
//			public void execute(Fragment frag, Writer out) throws IOException {
//				// TODO Auto-generated method stub
//				
//			}
//		};
		
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
		
		@Auxiliary
		default SiteConfig getSite() {
			return context().site().config();
		}
		
		public static ImmutableMustacheWrapper.Builder builder() {
			return ImmutableMustacheWrapper.builder();
		}
	}
	
	@Immutable
	interface MustacheFormating {
		@Parameter
		Object value();
		
		public static MustacheFormating of(Object value) {
			return ImmutableMustacheFormating.of(value);
		}
	}
}
