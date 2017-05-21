package de.flapdoodle.solid.theme;

import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.generator.Document;

public interface Theme {
	Renderer rendererFor(String templateName);
	
	ImmutableList<Document> staticFiles(); 
}
