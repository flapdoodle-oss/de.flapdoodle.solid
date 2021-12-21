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
package de.flapdoodle.solid.theme.stringtemplate;

import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Lazy;

import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.content.render.MarkupRendererFactory;
import de.flapdoodle.solid.parser.content.Blob;
import de.flapdoodle.solid.theme.Context;
import de.flapdoodle.solid.theme.Paths;

@Immutable
public abstract class StringtemplateWrapper {
	protected abstract MarkupRendererFactory markupRenderFactory();
	protected abstract ImmutableList<Blob> allBlobs();
	public abstract Context context();
	
	@Auxiliary
	public StringtemplateBlobWrapper getSingle() {
		return allBlobs().size()==1 ? StringtemplateBlobWrapper.of(allBlobs().get(0),markupRenderFactory()) : null;
	}
	
	@Lazy
	public ImmutableList<StringtemplateBlobWrapper> getBlobs() {
		return allBlobs().stream()
				.map(b -> StringtemplateBlobWrapper.of(b, markupRenderFactory()))
				.collect(ImmutableList.toImmutableList());
	}
	
	@Lazy
	public StringtemplateSiteWrapper getSite() {
		return StringtemplateSiteWrapper.of(context().site().config());
	}
	
	@Auxiliary
	public String getUrl() {
		return context().paths().currentUrl();
	}

	@Auxiliary
	public Paths getPaths() {
		return context().paths();
	}

	public static ImmutableStringtemplateWrapper.Builder builder() {
		return ImmutableStringtemplateWrapper.builder();
	}
}