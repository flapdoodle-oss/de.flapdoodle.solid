package de.flapdoodle.solid.theme;

public interface BlobLinkFactory {

	String getLink();

	String getLink(String key, Object value);

}
