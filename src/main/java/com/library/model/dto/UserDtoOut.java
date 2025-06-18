package com.library.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoOut {

	private String name;
	private String surname;
	private String email;
	private List<LibroDtoOut> booksInPossession;
	
	@Override
	public String toString() {
		return "UserDtoOut [name=" + name + ", surname=" + surname + ", email=" + email + ", booksInPossession="
				+ booksInPossession + "]";
	}

}
