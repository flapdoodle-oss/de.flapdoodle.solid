package de.flapdoodle.solid.exceptions;

public abstract class AbstractRuntimeException extends RuntimeException {

	public AbstractRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public AbstractRuntimeException(String message) {
		super(message);
	}

	public AbstractRuntimeException(Throwable cause) {
		super(cause);
	}
	
}
