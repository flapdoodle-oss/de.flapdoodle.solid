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
import java.util.function.Supplier;
import java.util.stream.Stream;

import de.flapdoodle.types.ThrowingSupplier;
import de.flapdoodle.types.Try;

public abstract class Streams {

	public static <T> AutocloseStream<T> autoclose(Supplier<? extends Stream<T>> streamSupplier) {
		return new AutocloseStream<>(streamSupplier);
	}
	
	public static <T, E extends Exception> AutocloseThrowingStream<T,E> autocloseThrowing(ThrowingSupplier<? extends Stream<T>, E> streamSupplier) {
		return new AutocloseThrowingStream<T,E>(streamSupplier);
	}
	

	public static class AutocloseStream<T> {

		private Supplier<? extends Stream<T>> streamSupplier;

		public AutocloseStream(Supplier<? extends Stream<T>> streamSupplier) {
			this.streamSupplier = streamSupplier;
		}

		public <D> D apply(Function<Stream<T>, D> streamTransformation) {
			try (Stream<T> stream = streamSupplier.get()) {
				return streamTransformation.apply(stream);
			}
		}

		public void forEach(Consumer<T> action) {
			try (Stream<T> stream = streamSupplier.get()) {
				stream.forEach(action);
			}			
		}
	}

	public static class AutocloseThrowingStream<T, E extends Exception> {

		private ThrowingSupplier<? extends Stream<T>, E> streamSupplier;

		public AutocloseThrowingStream(ThrowingSupplier<? extends Stream<T>, E> streamSupplier) {
			this.streamSupplier = streamSupplier;
		}

		public <D> D apply(Function<Stream<T>, D> streamTransformation) throws E {
			try (Stream<T> stream = streamSupplier.get()) {
				return streamTransformation.apply(stream);
			}
		}

		public void forEach(Consumer<T> action) throws E {
			try (Stream<T> stream = streamSupplier.get()) {
				stream.forEach(action);
			}			
		}
	}
}
