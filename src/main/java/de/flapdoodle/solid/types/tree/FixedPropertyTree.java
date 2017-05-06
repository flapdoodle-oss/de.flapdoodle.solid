package de.flapdoodle.solid.types.tree;

import java.util.Date;

import org.immutables.value.Value.Immutable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

import de.flapdoodle.types.Either;

@Immutable
public abstract class FixedPropertyTree implements PropertyTree {

	protected abstract ImmutableMultimap<String, Either<Object, ? extends PropertyTree>> map();
	
	@Override
	public ImmutableSet<String> properties() {
		return map().keySet();
	}
	
	@Override
	public ImmutableList<Either<Object, ? extends PropertyTree>> get(String key) {
		return map().get(key).asList();
	}
	
	public static class Builder extends ImmutableFixedPropertyTree.Builder {
		public Builder putValue(String key, Object value) {
			return this.putMap(key, Either.left(value));
		}

		public Builder put(String key, PropertyTree value) {
			return this.putMap(key, Either.right(value));
		}
		
		public Builder put(String key, String value) {
			return putValue(key,value);
		}
		public Builder put(String key, Number value) {
			return putValue(key,value);
		}
		public Builder put(String key, Date value) {
			return putValue(key,value);
		}
	}
	
	public static Builder builder() {
		return new Builder();
	}
}
