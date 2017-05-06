package de.flapdoodle.solid.types.tree;

import java.util.List;
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
}
