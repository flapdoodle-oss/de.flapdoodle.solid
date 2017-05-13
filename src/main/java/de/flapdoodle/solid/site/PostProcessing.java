package de.flapdoodle.solid.site;

import java.util.regex.Pattern;

import org.immutables.value.Value.Immutable;

import com.google.common.collect.ImmutableList;

import de.flapdoodle.legacy.Optionals;
import de.flapdoodle.solid.site.ImmutablePostProcessing.Builder;
import de.flapdoodle.solid.types.tree.PropertyTree;

@Immutable
public interface PostProcessing {
	
	ImmutableList<Regex> regex();
	
	@Immutable
	interface Regex {
		String name();
		String source();
		Pattern pattern();
		String replacement();
	}
	
	public static PostProcessing of(PropertyTree tree) {
		Builder builder = ImmutablePostProcessing.builder();
		tree.find("regex").ifPresent(regex -> {
			regex.properties().forEach(name -> {
				PropertyTree regexConfig = Optionals.checkPresent(regex.find(name),"not a valid regex config: %s",name).get();
				String source = Optionals.checkPresent(regexConfig.find(String.class, "source"),"source not set for %s",name).get();
				String pattern = Optionals.checkPresent(regexConfig.find(String.class, "pattern"),"pattern not set for %s",name).get();
				String replacement = Optionals.checkPresent(regexConfig.find(String.class, "replacement"),"replacement not set for %s",name).get();
				
				builder.addRegex(ImmutableRegex.builder()
					.name(name)
					.source(source)
					.pattern(Pattern.compile(pattern))
					.replacement(replacement)
					.build());
			});
		});
		return builder.build();
	}

	public static PostProcessing empty() {
		return ImmutablePostProcessing.builder()
				.build();
	}
}
