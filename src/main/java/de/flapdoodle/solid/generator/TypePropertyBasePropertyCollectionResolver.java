package de.flapdoodle.solid.generator;

import java.util.Iterator;

import com.google.common.collect.ImmutableList;

import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.solid.types.properties.TypeProperties;
import de.flapdoodle.solid.types.properties.TypePropertiesLookup;
import de.flapdoodle.solid.types.properties.TypeProperty;
import de.flapdoodle.solid.types.tree.PropertyTree;
import de.flapdoodle.types.Either;

public class TypePropertyBasePropertyCollectionResolver implements PropertyCollectionResolver {
	
	private final TypePropertiesLookup lookup;

	public TypePropertyBasePropertyCollectionResolver(TypePropertiesLookup lookup) {
		this.lookup = lookup;
	}
	

	@Override
	public ImmutableList<?> resolve(PropertyTree tree, Iterable<String> path) {
		ImmutableList<String> pathAsList = ImmutableList.copyOf(path);
		return resolveInternal(tree, pathAsList);
	}


	private ImmutableList<?> resolveInternal(PropertyTree tree, ImmutableList<String> pathAsList) {
		if (!pathAsList.isEmpty()) {
			String currentPropertName = pathAsList.get(0);
			ImmutableList<String> leftPath = pathAsList.subList(1, pathAsList.size());
			ImmutableList<Either<Object, ? extends PropertyTree>> result = tree.findList(e -> Maybe.of(e), currentPropertName);
			long valueCount = result.stream().filter(e -> e.isLeft()).count();
			if (valueCount == result.size()) {
				return result.stream()
					.map((Either<Object, ? extends PropertyTree> e) -> e.left())
					.map((Object o) -> resolve(o, leftPath.iterator(), lookup))
					.filter(Maybe::isPresent)
					.map(Maybe::get)
					.collect(ImmutableList.toImmutableList());
			} else {
				if (valueCount==0) {
					return result.stream()
						.map(e -> e.right())
						.flatMap(p -> resolveInternal(p, leftPath).stream())
						.collect(ImmutableList.toImmutableList());
				}
			}
		}
		return ImmutableList.of();
	}

	private static <T> Maybe<?> resolve(T instance, Iterator<? extends String> iterator, TypePropertiesLookup lookup) {
		if (!iterator.hasNext()) {
			return Maybe.of(instance);
		}
		
		Maybe<TypeProperties<T>> properties = lookup.propertiesOf((Class<T>) instance.getClass());
		if (properties.isPresent()) {
			TypeProperties<T> typeProperties = properties.get();
			Maybe<TypeProperty<T, ?>> propertyOf = typeProperties.of(iterator.next());
			if (propertyOf.isPresent()) {
				Object newInstance = propertyOf.get().propertyOf(instance);
				return resolve(newInstance, iterator, lookup);
			}
		}
		return Maybe.empty();
	}

}
