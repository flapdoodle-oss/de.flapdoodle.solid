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
