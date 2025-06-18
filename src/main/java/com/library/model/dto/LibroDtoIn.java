package com.library.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class LibroDtoIn {
    private String identificativoIsbn;
    private String titolo;
    private String tipologia;
    private String autore;
    private String linguaggio;
    private double costo;

	 @Override
	    public String toString() {
	        return "LibroDtoIn{" +
	                "identificativoIsbn='" + identificativoIsbn + '\'' +
	                ", titolo='" + titolo + '\'' +
	                ", tipologia='" + tipologia + '\'' +
	                ", autore='" + autore + '\'' +
	                ", linguaggio='" + linguaggio + '\'' +
	                ", costo=" + costo +
	                '}';
	    }
}
