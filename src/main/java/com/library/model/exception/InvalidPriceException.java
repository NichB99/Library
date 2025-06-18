package com.library.model.exception;

public class InvalidPriceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidPriceException() {
		super();
	}

	public InvalidPriceException(String message) {
		super(message);
	}

	public InvalidPriceException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidPriceException(Throwable cause) {
		super(cause);
	}

}
