package de.flapdoodle.solid.parser.content;

import java.util.Optional;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.site.PostProcessing;
import de.flapdoodle.solid.site.PostProcessing.Regex;
import de.flapdoodle.solid.types.tree.FixedPropertyTree;
import de.flapdoodle.solid.types.tree.FixedPropertyTree.Builder;
import de.flapdoodle.solid.types.tree.PropertyTree;

public class SiteConfigPostProcessor implements PostProcessor {

	private final PostProcessing config;

	public SiteConfigPostProcessor(PostProcessing config) {
		this.config = config;
	}

	@Override
	public Blob process(Blob src) {
		if (!config.regex().isEmpty()) {
			return ImmutableBlob.copyOf(src)
					.withMeta(process(src.meta(),config.regex()));
		}
		return src;
	}

	private static PropertyTree process(PropertyTree meta, ImmutableList<Regex> regex) {
		Builder builder = FixedPropertyTree.builder()
			.copyOf(meta);
		
		for (Regex r : regex) {
			Optional<String> sourceVal = meta.find(String.class, Splitter.on(".").split(r.source()));
			if (sourceVal.isPresent()) {
				String source=sourceVal.get();
				String result = r.pattern().matcher(source).replaceAll(r.replacement());
				builder.put(r.name(), result);
			}
		}
		
		return builder.build();
	}

	public static SiteConfigPostProcessor of(PostProcessing config) {
		return new SiteConfigPostProcessor(config);
	}
}
