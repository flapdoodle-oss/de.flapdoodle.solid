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
package de.flapdoodle.solid.types;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.immutables.value.Value.Auxiliary;
import org.immutables.value.Value.Immutable;
import org.immutables.value.Value.Parameter;

import com.google.common.base.MoreObjects;
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
	
	public T orElseNull() {
		return isPresent() ? this.get() : null;
	}
	
	public T orElseGet(T fallback) {
		return isPresent() ? this.get() : fallback;
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
		
		@Override
		public String toString() {
			return MoreObjects.toStringHelper(getClass()).toString();
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
	
	public static <T> Maybe<T> not() {
		return absent();
	}
	
	@Deprecated
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
