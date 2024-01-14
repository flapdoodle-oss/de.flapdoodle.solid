package de.flapdoodle.solid.theme.pebble2;

import io.pebbletemplates.pebble.attributes.ResolvedAttribute;
import io.pebbletemplates.pebble.node.ArgumentsNode;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;

public interface DynamicAttributeResolver {

	/**
	 * Returns <code>true</code> if the attribute can be
	 * provided given the specified name.
	 */
	boolean canProvideDynamicAttribute(Object attributeName);

	/**
	 * Returns the attribute given the specified name and arguments.
	 */
	Object getDynamicAttribute(Object attributeName, Object[] argumentValues);

//	ResolvedAttribute resolve(Object attributeNameValue, Object[] argumentValues, ArgumentsNode args, EvaluationContextImpl context,
//		String filename, int lineNumber);
}
