/*
 * Copyright (C) 2017
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.solid.theme.pebble2;

import com.google.common.collect.ImmutableList;
import de.flapdoodle.solid.types.Maybe;
import de.flapdoodle.solid.types.tree.PropertyTree;
import io.pebbletemplates.pebble.attributes.AttributeResolver;
import io.pebbletemplates.pebble.attributes.ResolvedAttribute;
import io.pebbletemplates.pebble.error.AttributeNotFoundException;
import io.pebbletemplates.pebble.error.PebbleException;
import io.pebbletemplates.pebble.extension.AbstractExtension;
import io.pebbletemplates.pebble.extension.Extension;
import io.pebbletemplates.pebble.node.ArgumentsNode;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class CustomPebbleAttributeResolver implements AttributeResolver {

	@Override
	public ResolvedAttribute resolve(Object instance, Object attribute, Object[] argumentValues, ArgumentsNode args, EvaluationContextImpl context,
		String filename, int lineNumber) {
//		return unwrapResolved(resolve(instance, attribute)).orElseThrow(() -> new AttributeNotFoundException(new RuntimeException("Instance: "+instance), "not found", attribute.toString(),lineNumber,filename));
		return unwrapResolved(resolve(instance, attribute))
			.orElse(null);
	}

	private static Optional<ResolvedAttribute> unwrapResolved(Optional<ResolvedAttribute> resolved) {
		return resolved.map(wrapped -> new ResolvedAttribute(unwrap(wrapped.evaluatedValue)));
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
			return Optional.of(new ResolvedAttribute(propertyTree.findList(Object.class, String.valueOf(attribute))));
		}
		if (instance instanceof Iterable) {
			if (String.valueOf(attribute).equals("_single")) {
				Iterator iterator = ((Iterable) instance).iterator();
				if (iterator.hasNext()) {
					Object value = iterator.next();
					if (!iterator.hasNext()) {
						return Optional.of(new ResolvedAttribute(value));
					}
				}
				return Optional.of(new ResolvedAttribute(null));
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
