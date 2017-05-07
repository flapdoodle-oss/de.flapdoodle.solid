package de.flapdoodle.solid.types.tree;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;

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
		return find(type, FluentIterable.from(path));
	}
	
	default <T> Optional<T> find(Class<T> type, Iterable<String> path) {
		Iterator<String> iterator = path.iterator();
		Preconditions.checkArgument(iterator.hasNext(),"empty path: %s",path);
		Optional<PropertyTree> current = Optional.of(this);
		
		while (current.isPresent() && iterator.hasNext()) {
			String key=iterator.next();
			List<Either<Object, ? extends PropertyTree>> list = current.get().get(key);
			if (list.size()==1) {
				Either<Object, ? extends PropertyTree> value = list.get(0);
				if (value.isLeft()) {
					if (!iterator.hasNext()) {
						Object leftValue = value.left();
						return type.isInstance(leftValue) ? Optional.of((T) leftValue) : Optional.empty();
					}
				} else {
					if (iterator.hasNext()) {
						current=Optional.of(value.right());
					}
				}
			} else {
				break;
			}
		}
		
		return Optional.empty();
	}

}
