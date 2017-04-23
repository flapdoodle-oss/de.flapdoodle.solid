package de.flapdoodle.solid.parser.content;

import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.immutables.value.Value;
import org.immutables.value.Value.Parameter;

import com.google.common.annotations.VisibleForTesting;

import de.flapdoodle.solid.io.Filenames;

public class DefaultBlobParser implements BlobParser {

	private static final Pattern TOML_START = Pattern.compile("(?m)(?d)^\\+{3}");
	private static final Pattern TOML_END = TOML_START;
	private static final Pattern YAML_START = Pattern.compile("(?m)(?d)^\\-{3}");
	private static final Pattern YAML_END = YAML_START;

	@Override
	public Optional<Blob> parse(Path path, String content) {
		String filename = Filenames.filenameOf(path);
		String extension = Filenames.extensionOf(filename);
		Optional<ContentType> contentType = ContentType.ofExtension(extension);
		if (contentType.isPresent()) {
			Optional<MetaAndContent> metaAndContent = findToml(content);
			if (metaAndContent.isPresent()) {
				System.out.println("got toml");
			} else {
				metaAndContent = findYaml(content);
				if (metaAndContent.isPresent()) {
					System.out.println("got yaml");
				}
			}
		}
		
		return Optional.empty();
	}

	@VisibleForTesting
	protected static Optional<MetaAndContent> findToml(String content) {
		return findMeta(TOML_START, TOML_END, content);
	}

	@VisibleForTesting
	protected static Optional<MetaAndContent> findYaml(String content) {
		return findMeta(YAML_START, YAML_END, content);
	}
	
	@VisibleForTesting
	protected static Optional<MetaAndContent> findMeta(Pattern startMarker, Pattern endMarker, String content) {
		Matcher startMatcher = startMarker.matcher(content);
		
		if (startMatcher.find()) {
			int startOfMeta = startMatcher.end();
			
			Matcher endMatcher = endMarker.matcher(content);
			if (endMatcher.find(startOfMeta)) {
				int endOfMeta=endMatcher.start();
				int startOfContent=endMatcher.end();
				return Optional.of(MetaAndContent.of(content.substring(startOfMeta, endOfMeta), content.substring(startOfContent)));
			}
		}
		
		return Optional.empty();
	}
	
	@Value.Immutable
	interface MetaAndContent {
		@Parameter
		String meta();
		@Parameter
		String content();
		
		public static MetaAndContent of(String meta, String content) {
			return ImmutableMetaAndContent.of(meta, content);
		}
	}
}
