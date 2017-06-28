package de.flapdoodle.solid.theme;

import de.flapdoodle.solid.types.Maybe;

public interface LinkFactory {

	Maybe<? extends BlobsLinkFactory> byId(String id);

}
