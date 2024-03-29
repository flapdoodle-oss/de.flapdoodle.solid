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
package de.flapdoodle.solid.theme.pebble2;

import de.flapdoodle.solid.content.render.MarkupRenderer;
import de.flapdoodle.solid.content.render.MarkupRendererFactory;
import de.flapdoodle.solid.content.render.RenderContext;
import de.flapdoodle.solid.parser.content.Blob;
import de.flapdoodle.solid.theme.LinkFactories;
import de.flapdoodle.solid.theme.pebble2.ImmutablePebbleBlobWrapper;
import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.solid.types.tree.PropertyTree;
import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Lazy;
import org.immutables.value.Value.Parameter;

import java.util.function.Function;

@Immutable
public abstract class PebbleBlobWrapper {
	@Parameter
	protected abstract Blob blob();
	@Parameter
	protected abstract MarkupRenderer markupRenderer();
	@Parameter
	protected abstract LinkFactories.Named linkFactory();
	@Parameter
	protected abstract Function<String, Maybe<String>> urlMapping();

	@Auxiliary
	public PropertyTree getMeta() {
		return blob().meta();
	}

	@Lazy
	public String getAsHtml() {
		return markupRenderer().asHtml(RenderContext.builder()
				.urlMapping(urlMapping())
				.build(), blob().content());
	}

	@Lazy
	public String getHtml(int incrementHeading) {
		return markupRenderer().asHtml(RenderContext.builder()
				.urlMapping(urlMapping())
				.incrementHeading(incrementHeading)
				.build(), blob().content());
	}

	@Auxiliary
	public String getIntroHtml(int incrementHeading) {
		return getIntroHtml(incrementHeading,"");
	}

	@Auxiliary
	public String getIntroHtml(int incrementHeading, String afterContent) {
		return markupRenderer().asHtml(RenderContext.builder()
				.urlMapping(urlMapping())
				.incrementHeading(incrementHeading)
				.build(), introOf(blob().content())+afterContent);
	}

	private static String introOf(String content) {
		int idx=content.indexOf("<!--more-->");
		if (idx!=-1) {
			return content.substring(0, idx);
		}
		idx=content.indexOf("\n\n");
		if (idx!=-1) {
			return content.substring(0, idx);
		}
		return content;
	}
	@Auxiliary
	public LinkFactories.OneBlob getLinkTo(String id) {
		return linkFactory().byId(id).flatMap(f -> f.filterBy(blob())).orElseNull();
	}


	public static PebbleBlobWrapper of(Blob src, MarkupRendererFactory factory, LinkFactories.Named linkFactory, Function<String, Maybe<String>> urlMapping) {
		return ImmutablePebbleBlobWrapper.of(src, factory.rendererFor(src.contentType()), linkFactory, urlMapping);
	}
}
