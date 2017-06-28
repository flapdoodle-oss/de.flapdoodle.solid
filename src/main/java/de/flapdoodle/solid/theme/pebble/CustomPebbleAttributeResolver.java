package de.flapdoodle.solid.theme.pebble;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.mitchellbosecke.pebble.attributes.AttributeResolver;
import com.mitchellbosecke.pebble.attributes.ResolvedAttribute;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Extension;

import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.solid.types.tree.PropertyTree;

public class CustomPebbleAttributeResolver implements AttributeResolver {

	@Override
	public Optional<ResolvedAttribute> resolve(Object instance, Object attribute, Object[] argumentValues, boolean isStrictVariables, String filename,
			int lineNumber) throws PebbleException {
		
		return unwrapResolved(resolve(instance, attribute));
	}

	private static Optional<ResolvedAttribute> unwrapResolved(Optional<ResolvedAttribute> resolved) {
		return resolved.map(wrapped -> () -> unwrap(wrapped.evaluate()));
	}

	private static Object unwrap(Object value) {
		if (value instanceof Maybe) {
			Maybe<Object> maybe=(Maybe<Object>) value;
			return maybe.orElseNull();
		}
		return value;
	}

	private static Optional<ResolvedAttribute> resolve(Object instance, Object attribute) {
		if (instance instanceof PropertyTree) {
			PropertyTree propertyTree = (PropertyTree) instance;
			return Optional.of(new ResolvedAttribute() {
				
				@Override
				public Object evaluate() throws PebbleException {
					ImmutableList<Object> ret = propertyTree.findList(Object.class, String.valueOf(attribute));
//					return ret.isEmpty() 
//							? null 
//							: ret.size() == 1 
//								? ret.get(0) 
//								: ret;
					return ret;
				}
			});
		}
		if (instance instanceof Iterable) {
			if (String.valueOf(attribute).equals("_single")) {
				return Optional.of(new ResolvedAttribute() {
					
					@Override
					public Object evaluate() throws PebbleException {
						Iterator iterator = ((Iterable) instance).iterator();
						if (iterator.hasNext()) {
							Object value=iterator.next();
							if (!iterator.hasNext()) {
								return value;
							}
						}
						return null;
					}
				});
			}
		}
		
		return Optional.empty();
	}

	public Extension asExtension() {
		return new AbstractExtension() {
			@Override
			public List<AttributeResolver> getAttributeResolver() {
				return ImmutableList.of(CustomPebbleAttributeResolver.this);
			}
		};
	}
}
