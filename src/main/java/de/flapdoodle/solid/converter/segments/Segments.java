package de.flapdoodle.solid.converter.segments;

import java.util.Optional;
import java.util.function.Function;

import com.google.common.annotations.VisibleForTesting;

public abstract class Segments {

	public static String map(String src, Function<String, String> defaultTransformation, Matcher...matchers) {
		StringBuilder sb=new StringBuilder();
		int lastEnd=0;
		boolean doloop=true;
		do {
			Optional<Replacement> optReplacement = firstOf(src, lastEnd, matchers);
			if (optReplacement.isPresent()) {
				Replacement replacement = optReplacement.get();
				if (lastEnd<replacement.start()) {
					sb.append(defaultTransformation.apply(src.substring(lastEnd, replacement.start())));
				}
				sb.append(replacement.content());
				lastEnd=replacement.end();
			} else {
				if (lastEnd<src.length()) {
					sb.append(defaultTransformation.apply(src.substring(lastEnd)));
				}
				doloop=false;
			}
		} while (doloop);
		
		return sb.toString();
	}
	
	@VisibleForTesting
	protected static Optional<Replacement> firstOf(String src, int start, Matcher ... matchers) {
		for (Matcher matcher : matchers) {
			Optional<Replacement> replacement = matcher.find(src, start);
			if (replacement.isPresent()) {
				return replacement;
			}
		}
		return Optional.empty();
	}

}
