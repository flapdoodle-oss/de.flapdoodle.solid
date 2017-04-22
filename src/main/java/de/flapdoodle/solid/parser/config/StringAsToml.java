package de.flapdoodle.solid.parser.config;

import com.moandjiezana.toml.Toml;

public class StringAsToml {
	
	public static Toml parse(String src) {
		return new Toml().read(src);
	}
}
