package com.library.model.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import com.library.model.dto.LibroDtoIn;
import com.library.model.dto.LibroDtoOut;
import com.library.model.entity.Book;
import com.library.model.exception.BookAlreadyExistsException;
import com.library.model.exception.BookNotFoundException;
import com.library.model.exception.InvalidPriceException;
import com.library.model.repo.BookRepository;

@SpringBootTest
public class BookServTest {

	@InjectMocks
	private BookServ bookServ;

	@Mock
	private BookRepository bookRep;

	@BeforeEach
	public void setup() {
		MockitoAnnotations.openMocks(this);
	}

	/**
	 * Verifica che la creazione di un libro con parametri validi:
	 * - invochi `findByIdentificativoIsbn` che restituisce null (libro non esistente)
	 * - ritorni un DTO non nullo con titolo corretto
	 * - salvi il libro tramite `bookRep.save(...)`
	 */
	@Test
	void testCreateDtoInParam_success() {
		String title = "Test";
		String type = "Fantasy";
		String author = "Author";
		String language = "EN";
		double price = 10.0;
		
//		non mi piace vedere il Warning
		@SuppressWarnings("unused")
		String isbn = "Te-FAE";
		
		when(bookRep.findByIdentificativoIsbn(anyString())).thenReturn(null);

		LibroDtoIn dto = bookServ.createDtoInParam(title, type, author, language, price);

		assertNotNull(dto);
		assertEquals(title, dto.getTitolo());
		verify(bookRep).save(any(Book.class));
	}

	/**
	 * Verifica la validazione del prezzo nel metodo `createDtoInParam`.
	 * Se il prezzo è zero o negativo, viene sollevata un'eccezione InvalidPriceException.
	 */
	@Test
	void testCreateDtoInParam_invalidPrice() {
		assertThrows(InvalidPriceException.class, () -> {
			bookServ.createDtoInParam("Title", "Type", "Author", "EN", 0);
		});
	}

	/**
	 * Verifica la gestione del caso in cui si prova a creare un libro già esistente:
	 * - `findByIdentificativoIsbn(...)` restituisce un Book non nullo
	 * - viene sollevata un'eccezione BookAlreadyExistsException
	 */
	@Test
	void testCreateDtoInParam_bookAlreadyExists() {
		when(bookRep.findByIdentificativoIsbn(anyString())).thenReturn(new Book());
		assertThrows(BookAlreadyExistsException.class, () -> {
			bookServ.createDtoInParam("Title", "Type", "Author", "EN", 20.0);
		});
	}

	/**
	 * Verifica la modifica di un libro esistente:
	 * - `findByIdentificativoIsbn(...)` restituisce un Book
	 * - vengono aggiornati campi come titolo e prezzo
	 * - viene invocato `bookRep.save(...)`
	 * - il DTO restituito riflette la modifica del titolo
	 */
	@Test
	void testModifyBookParam_success() {
		String isbn = "123-abc";
		Book book = new Book();
		book.setIdentificativoIsbn(isbn);
		book.setCosto(10.0);

		when(bookRep.findByIdentificativoIsbn(isbn)).thenReturn(book);

		LibroDtoOut result = bookServ.modifyBookParam(isbn, "NewTitle", "Type", "Author", "EN", 15.0);

		assertEquals("NewTitle", result.getTitolo());
		verify(bookRep).save(any(Book.class));
	}

	/**
	 * Verifica che la modifica di un libro con prezzo negativo:
	 * - `findByIdentificativoIsbn(...)` restituisce un Book
	 * - il prezzo negativo causa un'eccezione InvalidPriceException
	 */
	@Test
	void testModifyBookParam_invalidPrice() {
		String isbn = "123";
		when(bookRep.findByIdentificativoIsbn(isbn)).thenReturn(new Book());

		assertThrows(InvalidPriceException.class, () -> {
			bookServ.modifyBookParam(isbn, null, null, null, null, -5);
		});
	}

	/**
	 * Verifica la ricerca di un libro esistente per titolo e ISBN:
	 * - `findByTitoloAndIdentificativoIsbn(...)` restituisce un Book
	 * - il DTO di output non è nullo e contiene il titolo corretto
	 */
	@Test
	void testFindBook_found() {
		String title = "Title";
		String isbn = "ISBN";
		Book book = new Book();
		book.setTitolo(title);
		book.setIdentificativoIsbn(isbn);
		book.setCosto(12.0);

		when(bookRep.findByTitoloAndIdentificativoIsbn(title, isbn)).thenReturn(book);

		LibroDtoOut dto = bookServ.findBook(title, isbn);
		assertNotNull(dto);
		assertEquals(title, dto.getTitolo());
	}

	/**
	 * Verifica il comportamento quando un libro NON viene trovato:
	 * - `findByTitoloAndIdentificativoIsbn(...)` restituisce null
	 * - viene sollevata un'eccezione BookNotFoundException
	 */
	@Test
	void testFindBook_notFound() {
		when(bookRep.findByTitoloAndIdentificativoIsbn(anyString(), anyString())).thenReturn(null);
		assertThrows(BookNotFoundException.class, () -> {
			bookServ.findBook("Title", "ISBN");
		});
	}

	/**
	 * Verifica la cancellazione di un libro esistente:
	 * - `findByIdentificativoIsbn(...)` restituisce un Book
	 * - la chiamata a `deleteBook(...)` non solleva eccezioni
	 * - viene invocato `bookRep.delete(...)`
	 */
	@Test
	void testDeleteBook_success() {
		String isbn = "isbn-001";
		Book book = new Book();
		when(bookRep.findByIdentificativoIsbn(isbn)).thenReturn(book);

		assertDoesNotThrow(() -> bookServ.deleteBook(isbn));
		verify(bookRep).delete(book);
	}

	/**
	 * Verifica il comportamento quando si tenta di eliminare un libro inesistente:
	 * - `findByIdentificativoIsbn(...)` restituisce null
	 * - viene sollevata
	 * un'eccezione BookNotFoundException
	 */
	@Test
	void testDeleteBook_notFound() {
		when(bookRep.findByIdentificativoIsbn("missing")).thenReturn(null);
		assertThrows(BookNotFoundException.class, () -> bookServ.deleteBook("missing"));
	}
	
}
