package de.flapdoodle.solid.mimetypes;

import de.flapdoodle.solid.types.ByteArray;

public interface MimeTypeResolver {
	String mimeTypeOf(String name, ByteArray content);
}
