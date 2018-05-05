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

import java.util.function.Function;

import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Lazy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.j2objc.annotations.AutoreleasePool;

import de.flapdoodle.solid.content.render.MarkupRendererFactory;
import de.flapdoodle.solid.parser.content.Blob;
import de.flapdoodle.solid.site.SiteConfig;
import de.flapdoodle.solid.theme.Context;
import de.flapdoodle.solid.theme.LinkFactories.Blobs;
import de.flapdoodle.solid.theme.Links;
import de.flapdoodle.solid.theme.Paths;
import de.flapdoodle.solid.types.Maybe;

@Immutable
public abstract class PebbleWrapper {
	protected abstract MarkupRendererFactory markupRenderFactory();
	protected abstract ImmutableList<Blob> allBlobs();
	public abstract Context context();

	@Lazy
	protected Function<String, Maybe<String>> urlMapping() {
		return context().site().config().urlRewrite().rewriter();
	}

	@Auxiliary
	public PebbleBlobWrapper getSingle() {
		return allBlobs().size()==1 ? PebbleBlobWrapper.of(allBlobs().get(0),markupRenderFactory(), context().linkFactory(), urlMapping()) : null;
	}

	@Auxiliary
	public ImmutableList<PebbleBlobWrapper> blobs() {
		return allBlobs().stream()
				.map(b -> PebbleBlobWrapper.of(b, markupRenderFactory(), context().linkFactory(), urlMapping()))
				.collect(ImmutableList.toImmutableList());
	}

	@Auxiliary
	public ImmutableList<PebbleBlobWrapper> blobs(int max) {
		return allBlobs().stream()
				.limit(max)
				.map(b -> PebbleBlobWrapper.of(b, markupRenderFactory(), context().linkFactory(), urlMapping()))
				.collect(ImmutableList.toImmutableList());
	}

	@Lazy
	public PebbleSiteWrapper getSite() {
		return PebbleSiteWrapper.of(context().site().config());
	}

	@Lazy
	public String getUrl() {
		return context().paths().currentUrl();
	}
	
	@Auxiliary
	public String linkTo(String path) {
		SiteConfig config = context().site().config();
		return Links.renderLink(config.baseUrl(), path, getUrl(), config.relativeLinks());
	}

	@Auxiliary
	public String linkToRoot() {
		return context().site().config().baseUrl();
	}

	@Lazy
	public Paths getPaths() {
		return context().paths();
	}

	@Lazy
	public ImmutableMap<String,Object> getProperties() {
		return context().pathProperties();
	}

	@Auxiliary
	public Blobs linksTo(String id) {
		return context().linkFactory().byId(id).orElseNull();
	}

	public static ImmutablePebbleWrapper.Builder builder() {
		return ImmutablePebbleWrapper.builder();
	}
}
