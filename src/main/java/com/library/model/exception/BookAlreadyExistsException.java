package com.library.model.exception;

public class BookAlreadyExistsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BookAlreadyExistsException() {
		super();
	}

	public BookAlreadyExistsException(String message) {
		super(message);
	}

	public BookAlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}

	public BookAlreadyExistsException(Throwable cause) {
		super(cause);
	}

}
