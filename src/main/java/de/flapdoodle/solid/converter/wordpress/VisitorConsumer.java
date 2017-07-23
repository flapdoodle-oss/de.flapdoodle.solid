package de.flapdoodle.solid.converter.wordpress;

import java.util.Map;
import java.util.function.BiConsumer;

import de.flapdoodle.solid.xml.Visitor;

public interface VisitorConsumer<T> extends BiConsumer<Visitor, T> {

	
	public static <T> VisitorConsumer<T> ofMap(Map<String, VisitorConsumer<T>> map) {
		return (visitor, scope) -> {
			VisitorConsumer<T> consumer = map.get(visitor.currentTagName());
			if (consumer!=null) {
				consumer.accept(visitor, scope);
			}
		};
	}
}
