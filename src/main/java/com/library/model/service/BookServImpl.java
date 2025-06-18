package com.library.model.service;

import com.library.model.dto.LibroDtoIn;
import com.library.model.dto.LibroDtoOut;
import com.library.model.entity.Book;
import com.library.model.entity.User;

public interface BookServImpl {

	public void deleteBook(String isbn);
	
	public LibroDtoOut removeBookDto (String title, String isbnId);
	
	public LibroDtoOut setUserDto (User user, String title, String isbnId);

	public LibroDtoOut findBook(String title, String isbnId);

	public LibroDtoOut modifyBookJson(String isbnId, LibroDtoIn updatedBook);

	public LibroDtoOut modifyBookParam(String isbnId, String title, String type, String author, String language, double price);

	public LibroDtoIn createDtoInParam(String title, String type, String author, String language, double price);
	
	public LibroDtoOut libroDtoOut(Book b);

}
