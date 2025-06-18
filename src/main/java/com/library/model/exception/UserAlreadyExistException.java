package com.library.model.exception;

public class UserAlreadyExistException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public UserAlreadyExistException() {
		super();
	}

	public UserAlreadyExistException(String message) {
		super(message);
	}

	public UserAlreadyExistException(String message, Throwable cause) {
		super(message, cause);
	}

	public UserAlreadyExistException(Throwable cause) {
		super(cause);
	}


}
