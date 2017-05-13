package de.flapdoodle.solid.parser.content;

public interface PostProcessor {
	Blob process(Blob src);
}
