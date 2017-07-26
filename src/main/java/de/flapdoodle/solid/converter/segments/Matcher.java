package de.flapdoodle.solid.converter.segments;

import java.util.Optional;
import java.util.function.Function;

public interface Matcher {
	Optional<Replacement> find(String src, int currentPosition);
	
	public static Matcher of(String start, String end) {
		return of(start, end, s -> s);
	}
	
	public static Matcher of(String start, String end, Function<String, String> map) {
		return (src, pos) -> {
			int idxA=src.indexOf(start, pos);
			if (idxA!=-1) {
				int idxE = src.indexOf(end, idxA+start.length());
				if (idxE!=-1) {
					return Optional.of(Replacement.of(idxA, idxE+end.length(), map.apply(src.substring(idxA, idxE+end.length()))));
				}
			}
			return Optional.empty();
		};
	}
}
