package de.flapdoodle.solid.parser.content;

import java.nio.file.Path;
import java.util.Optional;

public interface BlobParser {
	Optional<Blob> parse(Path path, String content);
}
