package de.flapdoodle.solid.theme.pebble2;

import com.google.common.collect.ImmutableList;
import io.pebbletemplates.pebble.attributes.AttributeResolver;
import io.pebbletemplates.pebble.attributes.ResolvedAttribute;
import io.pebbletemplates.pebble.extension.AbstractExtension;
import io.pebbletemplates.pebble.extension.Extension;
import io.pebbletemplates.pebble.node.ArgumentsNode;
import io.pebbletemplates.pebble.template.EvaluationContextImpl;

import java.util.List;

public class Delegate2DynamicAttributeResolver implements AttributeResolver {

	@Override
	public ResolvedAttribute resolve(Object instance, Object attributeNameValue, Object[] argumentValues, ArgumentsNode args, EvaluationContextImpl context,
		String filename, int lineNumber) {
		if (instance instanceof DynamicAttributeResolver) {
			if (((DynamicAttributeResolver) instance).canProvideDynamicAttribute(attributeNameValue)) {
				return new ResolvedAttribute(((DynamicAttributeResolver) instance).getDynamicAttribute(attributeNameValue, argumentValues));
			}
//			return ((DynamicAttributeResolver) instance).resolve(attributeNameValue, argumentValues, args, context, filename, lineNumber);
		}
		return null;
	}

	public Extension asExtension() {
		return new AbstractExtension() {
			@Override
			public List<AttributeResolver> getAttributeResolver() {
				return ImmutableList.of(Delegate2DynamicAttributeResolver.this);
			}
		};
	}

}
