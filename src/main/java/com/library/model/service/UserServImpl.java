package com.library.model.service;

import com.library.model.dto.UserDtoIn;
import com.library.model.dto.UserDtoOut;

public interface UserServImpl {
	
	public String removeUser(String email);
	
	public UserDtoOut bookAssignment(String email, String title, String isbn);
	
	public UserDtoOut removeBook(String email, String bookName);
		
	public UserDtoOut modifyUserParams(String name, String surname, String email);

	public UserDtoOut modifyUserJson(String email, UserDtoIn uDtoIn);

	public UserDtoOut findUser(String email);

	public UserDtoIn createDtoin(String name, String surname, String email);

}
