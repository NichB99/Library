package com.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.library.model.dto.LibroDtoIn;
import com.library.model.dto.LibroDtoOut;
import com.library.model.service.BookServ;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/books")
public class BookController {
	
	/**
	 * TODO
	 * CRUD					<V>
	 *	CREATE				<V>
	 *	READ				<V>
	 *	UPDATE				<V>
	 *	DELETE				<V>
	 * OPERAZIONI BATCH		<X>	// Inserimento/rimozione multipla
	 * FILE READING			<X>	// Import/export CSV/Excel/PDF
	 * PAGINAZIONE & SORT	<X>	// Gestione risultati in modo scalabile
	 * RICERCA AVANZATA		<X>	// Filtro per titolo, autore, prezzo, lingua, tipo
	 * VALIDAZIONE INPUT	<V>
	 * STATISTICHE			<X>	// Totali, medie, libri per autore (o altro?).
	 * LOG ATTIVITÀ			<V>
	 * GESTIONE ERRORI		<V>
	 * SUPPORTO LINGUA		<X>	// Supporto multilingua nelle risposte
	 */
	
	@Autowired
	private BookServ serv;

	@Operation(summary = "Crea libro", description = "Inserisci un nuovo libro nel sistema con dei parametri")
	@PostMapping("/create")
	public LibroDtoIn createBookParam(@RequestParam String title, @RequestParam String type, @RequestParam String author, @RequestParam String language, @RequestParam double price) {

		return serv.createDtoInParam(title, type, author, language, price);
	}

	@Operation(summary = "Trova libro", description = "Trova il libro con gli stessi campi inseriti")
	@GetMapping("/find")
	public LibroDtoOut findBook(@RequestParam String title, @RequestParam String isbnId) {

		return serv.findBook(title, isbnId);
	}

	@Operation(summary = "Cancella libro", description = "Cancella il libro secondo i filtri inseriti")
	@DeleteMapping("/remove")
	public String deleteBook(@RequestParam String isbnId) {

		serv.deleteBook(isbnId);
		return "Libro con ISBN: " + isbnId + ", rimosso con successo.";
	}

	@Operation(summary = "Modifica libro", description = "Modifica i campi del libro tramite JSON")
	@PatchMapping("modify/{isbnId}")
	public LibroDtoOut modifyBookJson(@PathVariable String isbnId, @RequestBody LibroDtoIn updatedBook) {

		return serv.modifyBookJson(isbnId, updatedBook);
	}

	@Operation(summary = "Modifica libro", description = "Modifica i campi del libro tramite il codice ISBN")
	@PatchMapping("/modify/param")
	public LibroDtoOut modifyBook(@RequestParam String isbnId, @RequestParam(required = false) String title,
			@RequestParam(required = false) String type, @RequestParam(required = false) String author,
			@RequestParam(required = false) String language, @RequestParam(required = false) double price) {

		return serv.modifyBookParam(isbnId, title, type, author, language, price);

	}

}
