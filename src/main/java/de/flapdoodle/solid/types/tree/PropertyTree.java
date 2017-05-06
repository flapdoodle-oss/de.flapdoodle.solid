package de.flapdoodle.solid.types.tree;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import de.flapdoodle.types.Either;

//wie json

//key = value -> [value]
//key = [ ... ]
//key = { ... } -> [{}]
//value -> String | Number | Date
//key = [{}]|[value]

//map = key -> [ value | map ]
public interface PropertyTree {
	Set<String> properties();
	List<Either<Object, ? extends PropertyTree>> get(String key);
	
	default String prettyPrinted() {
		return PropertyTreePrinter.prettyPrinted(this);
	}
	
	default <T> Optional<T> find(Class<T> type, String ... path) {
		for (String key : path) {
			
		}
		return Optional.empty();
	}

}
