package de.flapdoodle.solid.theme;

import java.util.Optional;

public interface Paths {
	String currentUrl();

	Optional<Page> getPreviousPage();

	Optional<Page> getNextPage();
}
