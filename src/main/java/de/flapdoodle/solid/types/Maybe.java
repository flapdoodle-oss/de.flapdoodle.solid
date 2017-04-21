/**
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

import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

public abstract class Maybe<T> {

	public abstract boolean isPresent();
	
	public abstract <D> Maybe<D> flatmap(Function<T, Maybe<D>> map);
	
	public abstract T get();
	
	public abstract Optional<T> asGuava();
	
	public void ifPresent(Consumer<T> consumer) {
		if (isPresent()) {
			consumer.accept(get());
		}
	}
	
	private static final class Nothing<T> extends Maybe<T> {
		
		private Nothing() {
			// no instance
		}
		
		@Override
		public boolean isPresent() {
			return false;
		}
		
		@Override
		public <D> Maybe<D> flatmap(Function<T, Maybe<D>> map) {
			return nothing();
		}
		
		@Override
		public T get() {
			return Preconditions.checkNotNull(null,"nothing here");
		}
		
		@Override
		public Optional<T> asGuava() {
			return Optional.absent();
		}
	}
	
	private static final Nothing<?> NOTHING=new Nothing<>();
	
	private static final class Something<T> extends Maybe<T> {
		private final T value;
		
		private Something(T value) {
			this.value = Preconditions.checkNotNull(value);
		}
		
		@Override
		public boolean isPresent() {
			return true;
		}
		
		@Override
		public <D> Maybe<D> flatmap(Function<T, Maybe<D>> map) {
			return map.apply(value);
		}
		
		@Override
		public T get() {
			return value;
		}
		
		@Override
		public Optional<T> asGuava() {
			return Optional.of(value);
		}
	}

	public static <T> Maybe<T> of(T value) {
		return new Something<>(value);
	}

	public static <T> Maybe<T> fromNullable(T value) {
		return value != null ? new Something<T>(value) : nothing();
	}

	public static <T> Maybe<T> nothing() {
		return (Maybe<T>) NOTHING;
	}

	@Deprecated
	public static <T> Maybe<T> absent() {
		return nothing();
	}



}
