package de.flapdoodle.solid.theme.pebble;

import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import com.mitchellbosecke.pebble.extension.DynamicAttributeProvider;

import de.flapdoodle.solid.types.tree.PropertyTree;

@Immutable
public abstract class PebblePropertyTreeWrapper implements DynamicAttributeProvider {
	@Parameter
	protected abstract PropertyTree tree();
	
	@Override
	public boolean canProvideDynamicAttribute(Object attributeName) {
		return tree().find(Object.class, attributeName.toString()).isPresent();
	}
	
	@Override
	public Object getDynamicAttribute(Object attributeName, Object[] argumentValues) {
		return tree().find(Object.class, attributeName.toString()).orElseNull();
	}
	
	public static PebblePropertyTreeWrapper of(PropertyTree tree) {
		return ImmutablePebblePropertyTreeWrapper.of(tree);
	}
}
