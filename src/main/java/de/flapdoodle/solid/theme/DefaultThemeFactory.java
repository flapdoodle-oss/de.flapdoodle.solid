package de.flapdoodle.solid.theme;

import java.nio.file.Path;

import de.flapdoodle.solid.theme.mustache.MustacheTheme;

public class DefaultThemeFactory implements ThemeFactory {

	@Override
	public Theme of(Path themeDirectory) {
		return new MustacheTheme(themeDirectory);
	}

}
