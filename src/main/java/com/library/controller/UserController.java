package com.library.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.library.model.dto.UserDtoIn;
import com.library.model.dto.UserDtoOut;
import com.library.model.service.UserServ;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {
	
	/**
	 * TODO
	 * CRUD					<V>
	 *	CREATE				<V>
	 *	READ				<V>
	 *	UPDATE				<V>
	 *	DELETE				<V>
	 * OPERAZIONI BATCH		<X>	// Inserimento/rimozione multipla
	 * FILE READING			<X>	// Import/export CSV/Excel/PDF
	 * VALIDAZIONE INPUT	<V>
	 * LOG ATTIVITÀ			<V>
	 * GESTIONE ERRORI		<V>
	 * SUPPORTO LINGUA		<X>	// Supporto multilingua nelle risposte
	 * PAGINAZIONE & FILTRI	<X>	// Lista utenti paginata e filtrabile
	 * EXPORT PDF			<X>	// Esporta profilo utente in PDF
	 * VERIFICA ESISTENZA	<V>
	 * RESET DATI			<X>	// Resetta i dati utente (eccetto email)
	 * AUDIT TRAIL			<X>	// Storico modifiche dell’utente
	 * STATISTICHE SISTEMA	<X>	// Statistiche utenti/libri
	 * ENDPOINT TEST		<X>	// ping o health check
	 * CONTROLLO ACQUISTO	<X> // due o più utenti possono prelevare lo stesso libro (scambio di proprietà)
	 */

	@Autowired
	private UserServ user;

	@Operation(summary = "Registra un nuovo utente", description = "Registra un nuovo utente nel sistema")
	@PostMapping("/create")
	public UserDtoIn saveUser(@RequestParam @NotBlank String nome, @RequestParam @NotBlank String cognome,
			@RequestParam @NotBlank String email) {
		return user.createDtoin(nome, cognome, email);
	}

	@Operation(summary = "Assegna libro", description = "Assegna un libro a utente indicato")
	@PatchMapping("/buy")
	public UserDtoOut assignUserBook(@RequestParam @NotBlank String email, @RequestParam @NotBlank String bookName,
			@RequestParam @NotBlank String codiceISBN) {

		return user.bookAssignment(email, bookName, codiceISBN);

	}

	@Operation(summary = "Trova una persona", description = "Ricerca una persona tramite Email")
	@GetMapping("/find")
	public UserDtoOut findUser(@RequestParam @NotBlank String email) {

		return user.findUser(email);
	}

	@Operation(summary = "Rimuovi una persona", description = "rimuovi una persona tramite Email")
	@DeleteMapping("/delete")
	public String deleteUser(@RequestParam @NotBlank String email) {

		return user.removeUser(email);
	}

	@Operation(summary = "Modifica parametri", description = "modifica i parametri di una persona tramite Json")
	@PatchMapping("/modify/json")
	public UserDtoOut modifyUserJson(@RequestParam @NotBlank String email, @RequestBody UserDtoIn us) {

		return user.modifyUserJson(email, us);
	}

	@Operation(summary = "Modifica parametri", description = "modifica i parametri di una persona tramite campi specifici")
	@PatchMapping("/modify/param")
	public UserDtoOut modifyUserParam(@RequestParam @NotBlank String email,
										@RequestParam(required = false) @NotBlank String nome,
										@RequestParam(required = false) @NotBlank String cognome) {

		return user.modifyUserParams(nome, cognome, email);
	}

	@Operation(summary = "Rimuovi Libro", description = "Rimuovi il libro allegato a un User")
	@PatchMapping("/remove")
	public UserDtoOut removeBook(@RequestParam @NotBlank String email, @RequestParam @NotBlank String nomeLibro) {

		return user.removeBook(email, nomeLibro);
	}

}
