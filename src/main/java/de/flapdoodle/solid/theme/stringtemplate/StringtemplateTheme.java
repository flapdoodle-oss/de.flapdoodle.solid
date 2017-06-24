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
package de.flapdoodle.solid.theme.stringtemplate;

import java.nio.file.Path;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;

import org.stringtemplate.v4.DateRenderer;
import org.stringtemplate.v4.Interpreter;
import org.stringtemplate.v4.NumberRenderer;
import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.misc.ObjectModelAdaptor;
import org.stringtemplate.v4.misc.STNoSuchPropertyException;

import com.google.common.base.Preconditions;

import de.flapdoodle.solid.content.render.MarkupRendererFactory;
import de.flapdoodle.solid.formatter.Formatter;
import de.flapdoodle.solid.generator.Content;
import de.flapdoodle.solid.generator.Text;
import de.flapdoodle.solid.theme.AbstractTheme;
import de.flapdoodle.solid.theme.Context;
import de.flapdoodle.solid.theme.MapLike;
import de.flapdoodle.solid.theme.Renderer;
import de.flapdoodle.solid.theme.Renderer.Renderable;
import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.solid.types.tree.PropertyTree;

public class StringtemplateTheme extends AbstractTheme {

//	private final STGroupDir groupDir;

	public StringtemplateTheme(Path rootDir, PropertyTree config, MarkupRendererFactory markupRenderFactory) {
		super(rootDir,config,markupRenderFactory);
//		groupDir = new STGroupDir(rootDir.toAbsolutePath().toFile().toString());
	}
	
	@Override
	public Renderer rendererFor(String templateName) {
		return renderable -> render(renderable, rootDir, templateName);
	}
	
	private Content render(Renderable renderable, Path rootDir, String templateName) {
		String templateFile = rootDir.resolve(templateName+".stg").toAbsolutePath().toFile().toString();
		STGroupFile groupFile = new STGroupFile(templateFile,'$','$');
		initModelAdapter(groupFile);
		initRenderer(groupFile, renderable.context());
		
		ST template = Preconditions.checkNotNull(groupFile.getInstanceOf("page"),"page template is null: %s -> %s",templateName,groupFile);
		
		try {
			setContext(template, renderable);
			
			return Text.builder()
					.mimeType("text/html")
					.text(template.render())
					.build();
		} catch (RuntimeException rx) {
			throw new RuntimeException("template "+templateName, rx);
		}
	}

	private void initRenderer(STGroupFile groupFile, Context context) {
		Function<String, Maybe<Formatter>> formatterOfKey = key -> Maybe.ofNullable(context.site().config().formatters().formatters().get(key));
		
		TypesafeAttributeRenderer.of(Date.class, FormatterRendererAdapter.of(formatterOfKey, TypeRenderer.asTypeRenderer(new DateRenderer())))
			.register(groupFile);
		
		TypesafeAttributeRenderer.of(Number.class, FormatterRendererAdapter.of(formatterOfKey, TypeRenderer.asTypeRenderer(new NumberRenderer())))
			.register(groupFile);
	}
	
	private void initModelAdapter(STGroupFile groupFile) {
		TypesafeModelAdapter.of(MapLike.class, (m,name) -> m.get(name).orElseNull())
			.register(groupFile);
		
		TypesafeModelAdapter.of(PropertyTree.class, (t,name) -> t.find(Object.class, name).orElseNull())
			.register(groupFile);
		
		TypesafeModelAdapter.of(Optional.class, (o,name) -> {
				if (name.equals("is")) {
					return o.isPresent();
				}
				if (name.equals("get")) {
					return o.get();
				}
				return null;
			})
			.register(groupFile);
	}

	private void setContext(ST template, Renderable renderable) {
		template.add("this", StringtemplateWrapper.builder()
				.markupRenderFactory(markupRenderFactory)
				.context(renderable.context())
				.addAllAllBlobs(renderable.blobs())
				.build());
		
	}
	
	private static class MapLikeModelAdapter extends ObjectModelAdaptor {
		@Override
		public Object getProperty(Interpreter interpreter, ST self, Object o, Object property, String propertyName)
        throws STNoSuchPropertyException {
				MapLike mapLike=(MapLike) o;
				return mapLike.get(propertyName).orElseNull();
//			 return super.getProperty(interpreter,self,o,property,propertyName);
		}
	}
	
	
	
}
