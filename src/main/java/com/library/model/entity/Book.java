package com.library.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "books")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Book {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String identificativoIsbn;
	private String titolo;
	private String tipologia;
	private String autore;
	private String linguaggio;
	private double costo;
	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = true)
	private User proprietario;

	boolean isValid() {
		return  costo > 0 && identificativoIsbn != null && !identificativoIsbn.isBlank() && titolo != null
				&& !titolo.isBlank() && autore != null && !autore.isBlank() && linguaggio != null
				&& !linguaggio.isBlank();
	}
	
	@Override
	public String toString() {
	    return "Book{" +
	            "identificativoIsbn='" + identificativoIsbn + '\'' +
	            ", titolo='" + titolo + '\'' +
	            ", tipologia='" + tipologia + '\'' +
	            ", autore='" + autore + '\'' +
	            ", linguaggio='" + linguaggio + '\'' +
	            ", costo=" + costo +
	            '}';
	}
	
//	String getPresentation(){
//	return	"code: " + code + "\n" +
//			"title: " + title + "\n" +
//			"author: " + author + "\n" +
//			"price: " + price + " euro" + "\n" +
//			"page: " + pages +  "\n";
//}
	

//	boolean hasValidCategory() {
//		/*
//		 * getcategory() produce una string
//		 * e posso usarlo esattamente come una stringa
//		 * anche invocarne i metodi
//		 */
//		switch(getCategory().toUpperCase()) {
//			case "HRR":
//			case "SAG":
//			case "ROM":
//			case "FAN":
//			case "SCI":
//				return true;
//			default:
//				return false;
//		}
//
//	}
//	
//	boolean isAdult() {
//		//dal quarto carattere al quinto
//		String ageString = code.substring(3,5);
//		
//		//abbiamo applicato un metodo dell'oggetto di classe String
//		//per ottenere una SOTTOSTRINGA
//		
//		return Integer.parseInt(ageString) >= 18;
//	}


}
