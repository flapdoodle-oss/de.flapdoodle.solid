package de.flapdoodle.solid.types;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

public class OneOf {

	public static OneOfFactory type(Class<?> ...classes) {
		return new OneOfFactory(ImmutableSet.copyOf(classes));
	}
	
	public static class OneOfFactory {

		private final ImmutableSet<Class<?>> classes;

		public OneOfFactory(ImmutableSet<Class<?>> classes) {
			Iterables.everyCombinationOf(classes, (left,right) -> {
				Preconditions.checkArgument(!left.isAssignableFrom(right) && !right.isAssignableFrom(left),"class %s is parent/child of %s",left, right);
			});
			this.classes = classes;
		}

	}
}
