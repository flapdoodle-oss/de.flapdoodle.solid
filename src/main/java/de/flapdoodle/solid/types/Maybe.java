package de.flapdoodle.solid.types;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import com.google.common.base.Preconditions;

public abstract class Maybe<T> {

	public abstract T get();
	
	@Auxiliary
	public abstract boolean isPresent();
	
	@Auxiliary
	public abstract <D> Maybe<D> map(Function<T, D> map);

	@Auxiliary
	public abstract <D> Maybe<D> flatMap(Function<T, Maybe<D>> map);
	
	@Auxiliary
	public abstract Stream<T> asStream();
	
	@Auxiliary
	public Maybe<T> or(Supplier<Maybe<T>> fallback) {
		return isPresent() ? this : fallback.get();
	}
	
	@Auxiliary
	public T orElse(Supplier<T> fallback) {
		return isPresent() ? this.get() : fallback.get();
	}
	
	@Auxiliary
	public Optional<T> asOptional() {
		return isPresent() ? Optional.of(get()) : Optional.empty();
	}
	
	@Auxiliary
	public void ifPresent(Consumer<T> consumer) {
		if (isPresent()) {
			consumer.accept(get());
		}
	}

	
	static class None<T> extends Maybe<T> {
		@Override
		public T get() {
			throw new IllegalArgumentException("None");
		}
		
		@Override
		public boolean isPresent() {
			return false;
		}
		
		@Override
		public Stream<T> asStream() {
			return Stream.of();
		}
		
		@Override
		public <D> Maybe<D> map(Function<T, D> map) {
			return absent();
		}
		
		@Override
		public <D> Maybe<D> flatMap(Function<T, Maybe<D>> map) {
			return absent();
		}
		
		
	}
	
	@Immutable
	static abstract class Some<T> extends Maybe<T> {
		@Override
		@Parameter
		public abstract T get();
		
		@Override
		public boolean isPresent() {
			return true;
		}
		
		@Override
		public Stream<T> asStream() {
			return Stream.of(get());
		}
		
		@Override
		public <D> Maybe<D> map(Function<T, D> map) {
			return of(map.apply(get()));
		}
		
		@Override
		public <D> Maybe<D> flatMap(Function<T, Maybe<D>> map) {
			return map.apply(get());
		}
	}
	
	private static final None ABSENT=new None();
	
	public static <T> Maybe<T> absent() {
		return ABSENT;
	}
	
	@Deprecated
	public static <T> Maybe<T> empty() {
		return absent();
	}
	
	public static <T> Maybe<T> of(T value) {
		return ImmutableSome.of(value);
	}
	
	public static <T> Maybe<T> ofNullable(T value) {
		return value != null ? ImmutableSome.of(value) : absent();
	}
	
	public static <T> Maybe<T> fromOptional(Optional<T> src) {
		return src.isPresent() ? of(src.get()) : absent();
	}

	public static <T> Maybe<T> isPresent(Maybe<T> maybe, String message, Object ... errorMessageArgs) {
		Preconditions.checkArgument(maybe.isPresent(),message,errorMessageArgs);
		return maybe;
	}
}
