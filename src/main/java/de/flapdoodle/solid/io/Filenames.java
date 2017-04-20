package de.flapdoodle.solid.io;

import java.nio.file.Path;

public class Filenames {
	
	public static String filenameOf(Path path) {
		return path.getFileName().toString();
	}
}
