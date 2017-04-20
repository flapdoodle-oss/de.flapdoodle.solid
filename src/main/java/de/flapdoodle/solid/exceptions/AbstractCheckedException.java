package de.flapdoodle.solid.exceptions;

public abstract class AbstractCheckedException extends Exception {

	public AbstractCheckedException(String message, Throwable cause) {
		super(message, cause);
	}

	public AbstractCheckedException(String message) {
		super(message);
	}

	public AbstractCheckedException(Throwable cause) {
		super(cause);
	}
	
}
