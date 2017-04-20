package de.flapdoodle.solid.exceptions;

public class SomethingWentWrong extends AbstractRuntimeException {

	public SomethingWentWrong(Throwable cause) {
		super(cause);
	}

}
