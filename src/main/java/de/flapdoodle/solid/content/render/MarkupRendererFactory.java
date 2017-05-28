package de.flapdoodle.solid.content.render;

import de.flapdoodle.solid.parser.content.ContentType;

public interface MarkupRendererFactory {
	MarkupRenderer rendererFor(ContentType contentType);
	
	public static MarkupRendererFactory defaultFactory() {
		Markdown2Html markdown2Html = new Markdown2Html();
		
		return contentType -> {
			if (contentType==ContentType.Markdown) {
				return markdown2Html;
			}
			throw new IllegalArgumentException("not umplemented: "+contentType);
		};
	}
}
