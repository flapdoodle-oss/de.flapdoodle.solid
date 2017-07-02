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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.error.RuntimePebbleException;
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
			.extension(new CustomPebbleAttributeResolver().asExtension())
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
			
			template.evaluate(writer, Maps.newLinkedHashMap(ImmutableMap.of("it",it)));
			return writer.toString();
		} catch (PebbleException | IOException | RuntimePebbleException px) {
			throw new RuntimeException("rendering fails for "+template.getName(),px);
		}
	}
}
