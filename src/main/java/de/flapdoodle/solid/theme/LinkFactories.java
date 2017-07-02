package de.flapdoodle.solid.theme;

import com.google.common.collect.ImmutableSet;

import de.flapdoodle.solid.parser.content.Blob;
import de.flapdoodle.solid.types.Maybe;

public abstract class LinkFactories {

	public interface Named {
		Maybe<Blobs> byId(String id);
	}
	
	public interface Blobs {
		Maybe<OneBlob> filterBy(Blob blob);

		Filtered filter();
	}
	
	public interface OneBlob {
		String getLink();

		String getLink(String key, Object value);
	}

	
	public interface Filtered {

		Filtered by(String key, Object value);
		
		Filtered firstPage();

		ImmutableSet<Object> values(String key);

		String getLink();

		int count();

		Filtered orderBy(String key);

	}

}
