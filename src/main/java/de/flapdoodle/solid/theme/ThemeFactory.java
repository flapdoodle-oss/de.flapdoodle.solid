package de.flapdoodle.solid.theme;

import java.nio.file.Path;

import de.flapdoodle.solid.content.render.MarkupRendererFactory;
import de.flapdoodle.solid.parser.types.FiletypeParserFactory;

public interface ThemeFactory {
	Theme of(Path themeDirectory);
	
	public static ThemeFactory defaultFactory(FiletypeParserFactory filetypeParserFactory, MarkupRendererFactory markupRendererFactory) {
		return new DefaultThemeFactory(filetypeParserFactory, markupRendererFactory);
	}
}
