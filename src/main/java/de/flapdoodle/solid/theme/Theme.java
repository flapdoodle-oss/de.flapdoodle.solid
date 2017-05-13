package de.flapdoodle.solid.theme;

public interface Theme {
	Renderer rendererFor(String templateName);
}
