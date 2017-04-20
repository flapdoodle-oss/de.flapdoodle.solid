package de.flapdoodle.solid.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class In {

	public static String read(Path path) throws IOException {
		return new String(Files.readAllBytes(path),StandardCharsets.UTF_8);
	}
}
