package de.flapdoodle.solid.mimetypes;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;

import de.flapdoodle.solid.types.ByteArray;

public class FileExtensionMimeTypeResolver implements MimeTypeResolver {

	ImmutableMultimap<String,String> extensionMimeTypeMap=ImmutableMultimap.<String, String>builder()
			.putAll("image/jpeg","jpg","jpeg")
			.build()
			.inverse();
	
	@Override
	public String mimeTypeOf(String name, ByteArray content) {
		ImmutableCollection<String> mimeTypes = extensionMimeTypeMap.get(extensionOf(name));
		if (mimeTypes.size()==1) {
			return mimeTypes.iterator().next();
		}
		return "unknown/unknown";
	}

	private String extensionOf(String name) {
		int index = name.lastIndexOf('.');
		if (index!=-1) {
			return name.substring(index+1);
		}
		return name;
	}

}
