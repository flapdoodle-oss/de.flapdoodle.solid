package de.flapdoodle.solid.parser.config;

import java.nio.file.Path;
import java.util.function.Function;

import de.flapdoodle.solid.parser.config.ImmutableFilenamePatterns.Builder;
import de.flapdoodle.solid.site.SiteConfig;
import de.flapdoodle.types.Either;

public interface PathAsSiteConfig {

	Either<SiteConfig, FilenamePatterns> parse(Path path);
	
	default Function<Path, Either<SiteConfig, FilenamePatterns>> asFunction() {
		return path -> this.parse(path);
	}
	
	public static PathAsSiteConfig composite(PathAsSiteConfig ... list) {
		return path -> {
			Builder missedPatternsBuilder = FilenamePatterns.builder();
			for (PathAsSiteConfig l : list) {
				Either<SiteConfig, FilenamePatterns> config = l.parse(path);
				if (config.isLeft()) {
					return config;
				} else {
					missedPatternsBuilder.addAllPatterns(config.right().patterns());
				}
			}
			return Either.right(missedPatternsBuilder.build());
		};
	}
	
	public static PathAsSiteConfig defaults() {
		return composite(new TomlAsSiteConfig());
	}
}
