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
package de.flapdoodle.solid.theme;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;

import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.generator.Content;
import de.flapdoodle.solid.parser.content.Blob;

public interface Renderer {
	Content render(Renderable renderable);
	
	@Immutable
	@Style(deepImmutablesDetection=true)
	interface Renderable {
		ImmutableList<Blob> blobs();
		Context context();
		
		public static ImmutableRenderable.Builder builder() {
			return ImmutableRenderable.builder();
		}
	}
}
