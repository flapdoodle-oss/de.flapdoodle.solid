package de.flapdoodle.solid.theme;

import java.nio.file.Path;

public interface ThemeFactory {
	Theme of(Path themeDirectory);
	
	public static ThemeFactory defaultFactory() {
		return new DefaultThemeFactory();
	}
}
