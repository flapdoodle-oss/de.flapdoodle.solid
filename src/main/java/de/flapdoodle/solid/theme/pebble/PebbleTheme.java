/**
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
import com.mitchellbosecke.pebble.template.EvaluationContext;
import com.mitchellbosecke.pebble.template.PebbleTemplate;

import de.flapdoodle.solid.content.render.MarkupRendererFactory;
import de.flapdoodle.solid.content.render.RenderContext;
import de.flapdoodle.solid.generator.Text;
import de.flapdoodle.solid.parser.content.Blob;
import de.flapdoodle.solid.theme.AbstractTheme;
import de.flapdoodle.solid.theme.Renderer;
import de.flapdoodle.solid.theme.Renderer.Renderable;
import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.solid.types.tree.PropertyTree;

public class PebbleTheme extends AbstractTheme {

	private final PebbleEngine engine;

	public PebbleTheme(Path rootDir, PropertyTree config, MarkupRendererFactory markupRenderFactory) {
		super(rootDir, config, markupRenderFactory);
		
		this.engine = new PebbleEngine.Builder()
			.loader(new TemplateLoader(rootDir))
			.autoEscaping(false)
			.extension(new CustomExtension(markupRenderFactory), new CustomPebbleAttributeResolver().asExtension())
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

	@Deprecated
	private static class CustomExtension extends AbstractExtension {
		
		private final MarkupRendererFactory markupRendererFactory;

		public CustomExtension(MarkupRendererFactory markupRendererFactory) {
			this.markupRendererFactory = markupRendererFactory;
		}
		
		@Override
		public Map<String, Filter> getFilters() {
			return ImmutableMap.of("html",new BlobHtmlFilter(markupRendererFactory));
		}
	}
	
	private static class BlobHtmlFilter implements Filter {

		private final MarkupRendererFactory markupRendererFactory;

		public BlobHtmlFilter(MarkupRendererFactory markupRendererFactory) {
			this.markupRendererFactory = markupRendererFactory;
		}

		@Override
		public List<String> getArgumentNames() {
			return ImmutableList.of("level");
		}
		
		@Override
		public Object apply(Object input, Map<String, Object> args, PebbleTemplate self, EvaluationContext context, int lineNumber) throws PebbleException {
			if (input instanceof PebbleBlobWrapper) {
				PebbleBlobWrapper blob=(PebbleBlobWrapper) input;
				return blob.getHtml(getLevel(args.get("level")));
			}
			if (input instanceof Blob) {
				Blob blob=(Blob) input;
				markupRendererFactory.rendererFor(blob.contentType())
						.asHtml(RenderContext.builder()
						.urlMapping(s -> Maybe.absent())
						.incrementHeading(getLevel(args.get("level")))
						.build(), blob.content());
			}
			return input;
		}
		
		private static int getLevel(Object olevel) {
			if (olevel instanceof Integer) {
				return ((Integer) olevel).intValue();
			}
			if (olevel instanceof Long) {
				return ((Long) olevel).intValue();
			}
			return 0;
		}
	}
}
