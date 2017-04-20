package de.flapdoodle.solid.exceptions;

import java.nio.file.Path;

import com.google.common.base.Joiner;

public class NotASolidSite extends AbstractRuntimeException {

	public NotASolidSite(Path path, Iterable<String> triedConfigs) {
		super(asMessage(path, triedConfigs));
	}

	private static String asMessage(Path path, Iterable<String> triedConfigs) {
		return "could not find a matching config"+Joiner.on(",").join(triedConfigs)+" in "+path;
	}

}
