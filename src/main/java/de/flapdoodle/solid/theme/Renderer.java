package de.flapdoodle.solid.theme;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Style;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.flapdoodle.solid.generator.Content;
import de.flapdoodle.solid.parser.content.Blob;
import de.flapdoodle.solid.parser.content.Site;

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
	
	@Immutable
	interface Context {
		Site site();
		ImmutableMap<String, Object> pathProperties();
		
		public static ImmutableContext.Builder builder() {
			return ImmutableContext.builder();
		}
	}
}
