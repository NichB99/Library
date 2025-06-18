package com.library.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class LibroDtoOut {
	
	
	private String identificativoIsbn;
	private String titolo;
	private String tipologia;
	private String autore;
	private String linguaggio;
	private double costo;
	
	private String proprietario;

	public String toString() {
	    return "Book{" +
	            "identificativoIsbn='" + identificativoIsbn + '\'' +
	            ", titolo='" + titolo + '\'' +
	            ", tipologia='" + tipologia + '\'' +
	            ", autore='" + autore + '\'' +
	            ", linguaggio='" + linguaggio + '\'' +
	            ", costo=" + costo +
	            ", proprietario=" + proprietario +
	            '}';
	}
}
