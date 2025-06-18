package com.library.model.exception;

public class MaxBooksException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public MaxBooksException() {
		super();
	}

	public MaxBooksException(String message) {
		super(message);
	}

	public MaxBooksException(String message, Throwable cause) {
		super(message, cause);
	}

	public MaxBooksException(Throwable cause) {
		super(cause);
	}


}