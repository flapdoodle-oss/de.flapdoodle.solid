package de.flapdoodle.solid.theme.pebble;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import de.flapdoodle.solid.content.render.MarkupRendererFactory;
import de.flapdoodle.solid.generator.Text;
import de.flapdoodle.solid.theme.AbstractTheme;
import de.flapdoodle.solid.theme.Renderer;
import de.flapdoodle.solid.theme.Renderer.Renderable;
import de.flapdoodle.solid.types.tree.PropertyTree;

public class PebbleTheme extends AbstractTheme {

	private final PebbleEngine engine;

	public PebbleTheme(Path rootDir, PropertyTree config, MarkupRendererFactory markupRenderFactory) {
		super(rootDir, config, markupRenderFactory);
		
		this.engine = new PebbleEngine.Builder()
			.loader(new TemplateLoader(rootDir))
			.autoEscaping(false)
			.extension(new CustomExtension())
			.build();
	}

	@Override
	public Renderer rendererFor(String templateName) {
		try {
			PebbleTemplate template = engine.getTemplate(templateName+".html");
			
			return renderable -> Text.builder()
					.mimeType("text/html")
					.text(render(renderable, template))
					.build();
		}
		catch (PebbleException e) {
			throw new RuntimeException("could not render "+templateName,e);
		}
		
	}

	private String render(Renderable renderable, PebbleTemplate template) {
		try {
			StringWriter writer=new StringWriter();
			
			PebbleWrapper it = PebbleWrapper.builder()
					.markupRenderFactory(markupRenderFactory)
					.context(renderable.context())
					.addAllAllBlobs(renderable.blobs())
					.build();
			
			template.evaluate(writer, ImmutableMap.of("it",it));
			return writer.toString();
		} catch (PebbleException | IOException px) {
			throw new RuntimeException("rendering fails",px);
		}
	}

	private static class CustomExtension extends AbstractExtension {
		@Override
		public Map<String, Filter> getFilters() {
			return ImmutableMap.of("html",new BlobHtmlFilter());
		}
	}
	
	private static class BlobHtmlFilter implements Filter {

		@Override
		public List<String> getArgumentNames() {
			return ImmutableList.of("level");
		}

		@Override
		public Object apply(Object input, Map<String, Object> args) {
			if (input instanceof PebbleBlobWrapper) {
				PebbleBlobWrapper blob=(PebbleBlobWrapper) input;
				Object olevel = args.get("level");
				if (olevel instanceof Integer) {
					return blob.getHtml(((Integer) olevel).intValue());
				}
				if (olevel instanceof Long) {
					return blob.getHtml(((Long) olevel).intValue());
				}
				return blob.getHtml(0);
			}
			return input;
		}
		
	}
}
