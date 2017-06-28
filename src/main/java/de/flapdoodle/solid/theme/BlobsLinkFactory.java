package de.flapdoodle.solid.theme;

import de.flapdoodle.solid.parser.content.Blob;
import de.flapdoodle.solid.types.Maybe;

public interface BlobsLinkFactory {

	Maybe<BlobLinkFactory> filterBy(Blob blob);

}
