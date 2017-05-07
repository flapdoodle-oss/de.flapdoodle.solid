package de.flapdoodle.solid.parser.regex;

import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.flapdoodle.types.Either;

public abstract class Patterns {

	private Patterns() {
		// no instance
	}
	
	public static void parse(Pattern pattern, String src, Consumer<Either<String, Matcher>> consumer) {
		Matcher matcher = pattern.matcher(src);
		int lastEnd=0;
		while (matcher.find()) {
			consumer.accept(Either.left(src.substring(lastEnd,matcher.start())));
			consumer.accept(Either.right(matcher));
			lastEnd=matcher.end();
		}
		consumer.accept(Either.left(src.substring(lastEnd)));
	}
}
