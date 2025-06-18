package com.library.model.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import com.library.model.dto.LibroDtoIn;
import com.library.model.dto.LibroDtoOut;
import com.library.model.entity.Book;
import com.library.model.entity.User;
import com.library.model.exception.BookAlreadyExistsException;
import com.library.model.exception.BookNotFoundException;
import com.library.model.exception.InvalidPriceException;
import com.library.model.repo.BookRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableCaching
@Service
public class BookServ implements BookServImpl{

	@Autowired
	private BookRepository bookRep;

// ----------------------------------------------------- BOOK -------------------------------------------------------------

	/**
	 * Crea un nuovo libro e lo salva nel sistema, se non esiste già.
	 * <p>
	 * Il libro viene creato solo se il prezzo è maggiore di zero e non esiste già un libro con lo stesso ISBN.
	 * In caso contrario, viene sollevata un'eccezione.
	 * </p>
	 *
	 * @param title     il titolo del libro
	 * @param type      la tipologia (genere) del libro
	 * @param author    l'autore del libro
	 * @param language  la lingua in cui è scritto il libro
	 * @param isbnId    l'identificativo ISBN del libro
	 * @param price     il prezzo del libro (deve essere maggiore di 0)
	 * @throws InvalidPriceException se il prezzo è non valido
	 * @throws BookAlreadyExistsException se il libro è già presente nel sistema
	 * @author Nicholas
	 * @version 1.0.0
	 */
	@Transactional
	private void createBook(String title, String type, String author, String language, String isbnId, double price) {

		Book book = Book.builder().titolo(title).tipologia(type).autore(author).linguaggio(language).costo(price).identificativoIsbn(isbnId).build();

		validatePrice(price);

		if (finderType1(isbnId) != null) {
			
			log.error("il codice: " + isbnId + " risulta già presente nel sistema");
			throw new BookAlreadyExistsException("Libro già presente nel sistema con ISBN: " + isbnId);
		} else {
			log.info("il libro è stato correttamente creato: " + book.toString());
			bookRep.save(book);
		}

	}


	/**
	 * Cerca un libro nel repository tramite identificativo ISBN.
	 *
	 * @param isbnId identificativo ISBN del libro
	 * @return il libro trovato, oppure {@code null} se non esiste
	 * @author Nicholas
	 * @version 1.0.0
	 */
	@Cacheable("bookFinderCache")
	private Book finderType1(String isbnId) {

		Book b = null;
		b = bookRep.findByIdentificativoIsbn(isbnId);
		log.info("libro trovato con codice: " + isbnId);
		return b;
	}

	/**
	 * Cerca un libro nel repository tramite titolo e identificativo ISBN.
	 *
	 * @param title  titolo del libro
	 * @param isbnId identificativo ISBN del libro
	 * @return il libro trovato
	 * @throws BookNotFoundException se il libro non viene trovato
	 * @author Nicholas
	 * @version 1.0.0
	 */
	@Cacheable(value = "bookFinderByTitleCache", key = "#title + '-' + #isbnId")
	private Book finderType2(String title, String isbnId) {

		Book b = null;

		b = bookRep.findByTitoloAndIdentificativoIsbn(title, isbnId);
		if (b == null) {
			log.error("Il libro " + title + " non è stato trovato con codice: " + isbnId);
			throw new BookNotFoundException("Libro non trovato");
		}
		
		log.info(title + " con codice: " + isbnId + " è stato trovato");
		return b;
	}
	
	/**
	 * Assegna un utente come proprietario di un libro identificato da titolo e ISBN.
	 * Il libro viene aggiornato e salvato nel repository.
	 *
	 * @param user   l'utente da assegnare come proprietario
	 * @param title  il titolo del libro
	 * @param isbnId l'ISBN del libro
	 * @return l'oggetto {@link Book} aggiornato con il nuovo proprietario
	 * @author Nicholas
	 * @version 1.0.0
	 */
	@Transactional
	private Book setUser(User user, String title, String isbnId) {
		Book b = finderType2(title, isbnId);
		b.setProprietario(user);
		log.info(title + " correttamente aggiunto: " + b.getProprietario());
		bookRep.save(b);
		return b;
	}
	
	/**
	 * Rimuove il proprietario da un libro identificato da titolo e ISBN.
	 * Il libro viene aggiornato e salvato nel repository.
	 *
	 * @param title  il titolo del libro
	 * @param isbnId l'ISBN del libro
	 * @return l'oggetto {@link Book} aggiornato senza proprietario
	 * @author Nicholas
	 * @version 1.0.0
	 */
	@Transactional
	private Book removeBook(String title, String isbnId) {
		Book b = finderType2(title, isbnId);
		b.setProprietario(null);
		log.info(title + " correttamente rimosso");
		bookRep.save(b);
		return b;
	}

	/**
	 * Genera un identificativo ISBN personalizzato in base ai dati del libro.
	 *
	 * @param title    titolo del libro
	 * @param type     tipologia del libro
	 * @param author   autore del libro
	 * @param language lingua del libro
	 * @return una stringa ISBN generata
	 * @author Nicholas
	 * @version 1.0.0
	 */
	@Cacheable("isbnCache")
	private String createIsbn(String title, String type, String author, String language) {

		String isbn = "";
		isbn += title.substring(0, 2);
		isbn += "-";
		isbn += type.substring(0, 1);
		isbn += author.substring(0, 1);
		isbn += language.substring(0, 1);

		log.info("Codice isbn creato: " + isbn);
		return isbn;
	}
	
	/**
	 * Elimina un libro dal sistema in base all'identificativo ISBN.
	 *
	 * @param isbn identificativo ISBN del libro da eliminare
	 * @throws BookNotFoundException se il libro non è presente nel sistema
	 * @author Nicholas
	 * @version 1.0.0
	 */
	@Override
	@Transactional
	public void deleteBook(String isbn) {

		Book b = finderType1(isbn);
		if (b == null) {
			log.error("Libro non presente: " + isbn);
			throw new BookNotFoundException("Libro non presente nel sistema");
		}
		log.info("Libro con codice: " + isbn + " Rimosso");
		bookRep.delete(b);

	}

	/**
	 * Valida il prezzo del libro.
	 *
	 * @param p prezzo da validare
	 * @throws InvalidPriceException se il prezzo è minore o uguale a zero
	 * @author Nicholas
	 * @version 1.0.0
	 */
	private void validatePrice(double p) {

		boolean valid = p > 0;
		if (!valid) {
			log.error("Il prezzo inserito non è valido: " + p);
			throw new InvalidPriceException("Il prezzo non è valido");
		}
		
	}

// ----------------------------------------------------- DTO OUT ----------------------------------------------------------

	/**
	 * Cerca un libro e lo restituisce in formato DTO, utilizzando caching.
	 *
	 * @param title  titolo del libro
	 * @param isbnId identificativo ISBN del libro
	 * @return DTO contenente le informazioni del libro
	 * @throws BookNotFoundException se il libro non viene trovato
	 * @author Nicholas
	 * @version 1.0.0
	 */
	@Override
	@Cacheable(value = "bookCache", key = "#title + '-' + #isbnId")
	public LibroDtoOut findBook(String title, String isbnId) {

		log.info("Ricerca con campi: " + title + " " + isbnId);
		return libroDtoOut(finderType2(title, isbnId));
	}

	/**
	 * Modifica un libro esistente con i dati provenienti da un oggetto JSON (DTO).
	 *
	 * @param isbnId      identificativo ISBN del libro da modificare
	 * @param updatedBook DTO contenente i dati aggiornati
	 * @return DTO aggiornato del libro
	 * @throws InvalidPriceException se il prezzo non è valido
	 * @throws BookNotFoundException se il libro non esiste
	 * @author Nicholas
	 * @version 1.0.0
	 */
	@Override
	@Transactional
	public LibroDtoOut modifyBookJson(String isbnId, LibroDtoIn updatedBook) {

		Book existingBook = finderType1(isbnId);

		Optional.ofNullable(updatedBook.getTitolo()).ifPresent(existingBook::setTitolo);
		Optional.ofNullable(updatedBook.getTipologia()).ifPresent(existingBook::setTipologia);
		Optional.ofNullable(updatedBook.getAutore()).ifPresent(existingBook::setAutore);
		Optional.ofNullable(updatedBook.getLinguaggio()).ifPresent(existingBook::setLinguaggio);
		Optional.ofNullable(updatedBook.getIdentificativoIsbn()).ifPresent(existingBook::setIdentificativoIsbn);

		validatePrice(updatedBook.getCosto());
		existingBook.setCosto(updatedBook.getCosto());

		log.info("dto modificato per intero:\n" + existingBook.toString());
		bookRep.save(existingBook);
		return libroDtoOut(existingBook);
	}

	/**
	 * Modifica un libro esistente usando parametri diretti (senza DTO in input).
	 *
	 * @param isbnId   identificativo ISBN del libro da modificare
	 * @param title    nuovo titolo (opzionale)
	 * @param type     nuova tipologia (opzionale)
	 * @param author   nuovo autore (opzionale)
	 * @param language nuova lingua (opzionale)
	 * @param price    nuovo prezzo
	 * @return DTO aggiornato del libro
	 * @throws InvalidPriceException se il prezzo è non valido
	 * @throws BookNotFoundException se il libro non esiste
	 * @author Nicholas
	 * @version 1.0.0
	 */
	@Override
	@Transactional
	public LibroDtoOut modifyBookParam(String isbnId, String title, String type, String author, String language,
			double price) {

		Book existingBook = finderType1(isbnId);

		Optional.ofNullable(title).ifPresent(existingBook::setTitolo);
		Optional.ofNullable(type).ifPresent(existingBook::setTipologia);
		Optional.ofNullable(author).ifPresent(existingBook::setAutore);
		Optional.ofNullable(language).ifPresent(existingBook::setLinguaggio);

		validatePrice(price);
		existingBook.setCosto(price);
		log.info("dto parametri modificati:\n" + existingBook.toString());
		bookRep.save(existingBook);
		return libroDtoOut(existingBook);
	}

	/**
	 * Converte un'entità {@code Book} in un DTO {@code LibroDtoOut}.
	 *
	 * @param b libro da convertire
	 * @return DTO contenente i dati del libro
	 * @author Nicholas
	 * @version 1.0.0
	 */
	@Override
	@Cacheable("dtoOutCache")
	public LibroDtoOut libroDtoOut(Book b) {

		LibroDtoOut dto = new LibroDtoOut();

		Optional.ofNullable(b.getTitolo()).ifPresent(dto::setTitolo);
		Optional.ofNullable(b.getTipologia()).ifPresent(dto::setTipologia);
		Optional.ofNullable(b.getAutore()).ifPresent(dto::setAutore);
		Optional.ofNullable(b.getLinguaggio()).ifPresent(dto::setLinguaggio);
		Optional.ofNullable(b.getIdentificativoIsbn()).ifPresent(dto::setIdentificativoIsbn);

		if (b.getProprietario() == null) {
			dto.setProprietario("Non venduto");
		} else {
			dto.setProprietario(b.getProprietario().getEmail());
		}

		if (b.getCosto() > 0)
			dto.setCosto(b.getCosto());

		log.info("dto Output creato: " + dto.toString());
		return dto;
	}
	
	/**
	 * Rimuove il proprietario da un libro identificato da titolo e ISBN, e restituisce un DTO aggiornato.
	 *
	 * @param title  il titolo del libro
	 * @param isbnId l'ISBN del libro
	 * @return un {@link LibroDtoOut} con il campo proprietario impostato a {@code null}
	 * @author Nicholas
	 * @version 1.0.0
	 */
	@Override
	public LibroDtoOut removeBookDto (String title, String isbnId) {
		
		LibroDtoOut dto = findBook(title, isbnId);
		removeBook(title, isbnId).getProprietario();
		dto.setProprietario(null);
		log.info("Proprietario rimosso: " + dto.getTitolo());
		return dto;
	}
	
	/**
	 * Assegna un utente come proprietario di un libro identificato da titolo e ISBN, 
	 * e restituisce un DTO aggiornato con l'e-mail del nuovo proprietario.
	 *
	 * @param user   l'utente da assegnare come proprietario
	 * @param title  il titolo del libro
	 * @param isbnId l'ISBN del libro
	 * @return un {@link LibroDtoOut} con il campo proprietario aggiornato all'e-mail dell'utente
	 * @author Nicholas
	 * @version 1.0.0
	 */
	@Override
	public LibroDtoOut setUserDto (User user, String title, String isbnId) {
		
		setUser(user, title, isbnId);
		LibroDtoOut dto = findBook(title, isbnId);
		dto.setProprietario(user.getEmail());
		
		log.info("Proprietario: " + dto.getProprietario());
		return dto;
	}

// ----------------------------------------------------- DTO IN -----------------------------------------------------------

	/**
	 * Crea un nuovo libro e lo salva nel sistema, se non esiste già.
	 *
	 * @param title    il titolo del libro
	 * @param type     la tipologia (genere) del libro
	 * @param author   l'autore del libro
	 * @param language la lingua del libro
	 * @param isbnId   l'identificativo ISBN
	 * @param price    il prezzo del libro (deve essere maggiore di 0)
	 * @throws InvalidPriceException se il prezzo è non valido
	 * @throws BookAlreadyExistsException se il libro è già presente
	 * @author Nicholas
	 * @version 1.0.0
	 */
	@Override
	public LibroDtoIn createDtoInParam(String title, String type, String author, String language, double price) {

		LibroDtoIn lDto = new LibroDtoIn();
		String isbn = createIsbn(title, type, author, language);
		lDto.setTitolo(title);
		lDto.setTipologia(type);
		lDto.setAutore(author);
		lDto.setLinguaggio(language);
		lDto.setIdentificativoIsbn(isbn);
		lDto.setCosto(price);
		
		log.info("libro dto creato:\n" + lDto.toString());

		createBook(title, type, author, language, isbn, price);

		return lDto;
	}

}
