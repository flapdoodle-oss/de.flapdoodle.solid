package de.flapdoodle.solid.content.render;

public interface MarkupRenderer {
	String asHtml(RenderContext context, String src);
}
