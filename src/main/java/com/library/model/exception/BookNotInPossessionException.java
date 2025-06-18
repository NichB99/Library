package com.library.model.exception;

public class BookNotInPossessionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BookNotInPossessionException() {
		super();
	}

	public BookNotInPossessionException(String message) {
		super(message);
	}

	public BookNotInPossessionException(String message, Throwable cause) {
		super(message, cause);
	}

	public BookNotInPossessionException(Throwable cause) {
		super(cause);
	}

}
