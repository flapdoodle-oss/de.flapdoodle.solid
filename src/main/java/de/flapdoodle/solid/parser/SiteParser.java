package de.flapdoodle.solid.parser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import de.flapdoodle.solid.exceptions.NotASolidSite;
import de.flapdoodle.solid.exceptions.SomethingWentWrong;
import de.flapdoodle.solid.parser.config.FilenamePatterns;
import de.flapdoodle.solid.parser.config.PathAsSiteConfig;
import de.flapdoodle.solid.site.SiteConfig;
import de.flapdoodle.types.Either;
import de.flapdoodle.types.Try;

public class SiteParser {
	
	private final SiteConfig siteConfig;

	public SiteParser(SiteConfig siteConfig) {
		this.siteConfig = Preconditions.checkNotNull(siteConfig);
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass())
				.addValue(siteConfig)
				.toString();
	}

	public static SiteParser parse(Path siteRoot) {
		List<Either<SiteConfig, FilenamePatterns>> parseResult = Try.supplier(() -> Files.list(siteRoot)
				.map(PathAsSiteConfig.defaults().asFunction())
				.collect(Collectors.toList()))
			.mapCheckedException(SomethingWentWrong::new)
			.get();
		
		List<SiteConfig> configs = parseResult.stream()
				.filter(Either::isLeft)
				.map(e -> e.left())
				.collect(Collectors.toList());
		
		Set<String> triedPatterns = parseResult.stream()
			.filter(t -> !t.isLeft())
			.map(e -> e.right())
			.flatMap(f -> f.patterns().stream())
			.collect(Collectors.toSet());
		
		if (configs.size()!=1) {
			throw new NotASolidSite(siteRoot, triedPatterns);
		}
		
		return new SiteParser(configs.get(0));
	}
}
