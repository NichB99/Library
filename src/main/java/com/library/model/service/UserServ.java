package com.library.model.service;

import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import com.library.model.dto.UserDtoIn;
import com.library.model.dto.UserDtoOut;
import com.library.model.entity.User;
import com.library.model.exception.BookNotInPossessionException;
import com.library.model.exception.MaxBooksException;
import com.library.model.exception.UserAlreadyExistException;
import com.library.model.exception.UserNotFoundException;
import com.library.model.repo.UserRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableCaching
@Service
public class UserServ implements UserServImpl {

	@Autowired
	UserRepository userRep;
	@Autowired
	BookServ bs;

// ----------------------------------------------------- USER -------------------------------------------------------------

	/**
	 * Crea e salva un nuovo utente nel database, se non già presente.
	 *
	 * @param name    il nome dell'utente
	 * @param surname il cognome dell'utente
	 * @param email   l'email dell'utente
	 * @throws UserAlreadyExistException se un utente con la stessa email esiste già
	 *                                   nel sistema
	 * @author Nicholas
	 * @version 1.0.0
	 */
	@Transactional
	private void createUser(String name, String surname, String email) {
		User p = User.builder().nome(name).cognome(surname).email(email).build();

		if (userRep.existsByEmail(email)) {
			log.error("Email " + email + " già registrata nel sistema");
			throw new UserAlreadyExistException("Email Gia registrata.");
		}
		log.info("Utente creato: " + p.toString());
		userRep.save(p);
	}

	/**
	 * Recupera un oggetto {@code User} dal database tramite email.
	 *
	 * @param email l'email dell'utente da cercare
	 * @return l'oggetto {@code User} corrispondente
	 * @throws UserNotFoundException se non viene trovato alcun utente con
	 *                               quell'email
	 * @author Nicholas
	 * @version 1.0.0
	 */
	private User finder(String email) {

		User user = userRep.findByEmail(email);
		if (user == null) {
			
			log.error("Utente con email: " + email + " non risulta registrato");
			throw new UserNotFoundException("Utente non registato con la email: " + email);
		}
		log.info("Utente trovato con email. " + email);
		return user;
	}

	/**
	 * Rimuove un utente dal sistema e tutti i libri attualmente in suo possesso.
	 * <p>
	 * Se l'utente possiede libri, questi verranno rimossi prima di cancellare
	 * l'utente.
	 * </p>
	 *
	 * @param email l'email dell'utente da rimuovere
	 * @return messaggio di conferma dell'operazione
	 * @throws UserNotFoundException se l'utente non è registrato
	 * @author Nicholas
	 * @version 1.0.0
	 */
	@Transactional
	@CacheEvict(value = "userCache", key = "#email")
	@Override
	public String removeUser(String email) {

		User u = finder(email);

		for (int i = 0; i < u.getBooksInPossession().size(); i++) {
			bs.removeBookDto(u.getBooksInPossession().get(i).getTitolo(),
					u.getBooksInPossession().get(i).getIdentificativoIsbn());
		}

		u.getBooksInPossession().clear();
		userRep.delete(u);
		log.info("Utente con email: " + email + " rimosso");
		return "Utente con email: " + email + " rimosso";
	}

// ----------------------------------------------------- USERDTOOUT -------------------------------------------------------

	/**
	 * Assegna un libro a un utente se non ha superato il limite massimo consentito.
	 * <p>
	 * Un utente non può avere più di 5 libri contemporaneamente.
	 * </p>
	 *
	 * @param email l'email dell'utente
	 * @param title il titolo del libro
	 * @param isbn  il codice ISBN del libro
	 * @return l'oggetto {@code UserDtoOut} aggiornato dopo l'assegnazione
	 * @throws MaxBooksException se l'utente ha già raggiunto il limite massimo
	 * @author Nicholas
	 * @version 1.0.0
	 */
	@Transactional
	@Override
	public UserDtoOut bookAssignment(String email, String title, String isbn) {

		User u = finder(email);

		if (u.canBorrowMoreBooks()) {
			
			log.error("Utente con email: " + email + " ha superato il massimo numero di libri in possesso");
			throw new MaxBooksException("Un utente non può avere più di 5 libri.");
		}
		UserDtoOut dto = userDtoOut(u);
		dto.getBooksInPossession().add(bs.setUserDto(u, title, isbn));
		userRep.save(u);
		
		log.info("Dto con email: " + email + " ha prelevato con successo: " + title);
		return dto;
	}

	/**
	 * Rimuove un libro in possesso dell'utente in base al titolo fornito.
	 *
	 * @param email    l'email dell'utente
	 * @param bookName il titolo del libro da rimuovere
	 * @return l'oggetto {@code UserDtoOut} aggiornato
	 * @throws BookNotInPossessionException se il libro non è tra quelli in possesso
	 *                                      dell'utente
	 * @author Nicholas
	 * @version 1.0.0
	 */
	@Transactional
	@Override
	public UserDtoOut removeBook(String email, String bookName) {

		UserDtoOut u = findUser(email);

		for (int i = 0; i < u.getBooksInPossession().size(); i++) {

			if (u.getBooksInPossession().get(i).getTitolo().equals(bookName)) {
				bs.removeBookDto(u.getBooksInPossession().get(i).getTitolo(),
						u.getBooksInPossession().get(i).getIdentificativoIsbn());
				u.getBooksInPossession().remove(i);
				
				log.info("libro: " + bookName + " rimosso da: " + email);
				return u;
			}

		}
		log.error("Il libro " + bookName + " non è in possesso a " + email);
		throw new BookNotInPossessionException("Errore: il libro non è in possesso dell'utente");
	}

	/**
	 * Modifica i dati dell'utente specificato tramite parametri testuali.
	 *
	 * @param name    il nuovo nome (opzionale)
	 * @param surname il nuovo cognome (opzionale)
	 * @param email   l'email dell'utente da modificare
	 * @return l'oggetto {@code UserDtoOut} aggiornato
	 * @throws UserNotFoundException se l'utente non è presente nel sistema con
	 *                               l'email fornita
	 * @author Nicholas
	 * @version 1.0.0
	 */
	@Transactional
	@CacheEvict(value = "userCache", key = "#email")
	@Override
	public UserDtoOut modifyUserParams(String name, String surname, String email) {

		User user = finder(email);

		modifyUser(user, name, surname, email);

		userRep.save(user);
		
		log.info("Utente correttamente modidiciato:" + user.toString());
		return userDtoOut(user);
	}

	/**
	 * Modifica i dati dell'utente specificato utilizzando un oggetto
	 * {@code UserDtoIn}.
	 *
	 * @param email  l'email dell'utente da aggiornare
	 * @param uDtoIn oggetto contenente i nuovi valori da aggiornare
	 * @return l'oggetto {@code UserDtoOut} aggiornato
	 * @throws UserNotFoundException se l'utente non è presente nel sistema con
	 *                               l'email fornita
	 * @author Nicholas
	 * @version 1.0.0
	 */
	@Transactional
	@CacheEvict(value = "userCache", key = "#email")
	@Override
	public UserDtoOut modifyUserJson(String email, UserDtoIn uDtoIn) {

		User user = finder(email);
		modifyUser(user, uDtoIn.getName(), uDtoIn.getSurname(), uDtoIn.getEmail());

		userRep.save(user);
		log.info("Utente dto correttamente modificato");
		return userDtoOut(user);
	}

	/**
	 * Modifica i parametri dell'oggetto {@code User} solo se i valori non sono
	 * null.
	 *
	 * @param user    l'oggetto utente da modificare
	 * @param name    il nuovo nome (può essere null)
	 * @param surname il nuovo cognome (può essere null)
	 * @param email   il nuovo indirizzo email (può essere null)
	 * @author Nicholas
	 * @version 1.0.0
	 */
	private void modifyUser(User user, String name, String surname, String email) {

		Optional.ofNullable(name).ifPresent(user::setNome);
		Optional.ofNullable(surname).ifPresent(user::setCognome);
		Optional.ofNullable(email).ifPresent(user::setEmail);

	}

	/**
	 * Converte un oggetto {@code User} in un oggetto {@code UserDtoOut}, includendo
	 * i libri in possesso.
	 *
	 * @param u l'oggetto {@code User} da convertire
	 * @return l'oggetto {@code UserDtoOut} risultante
	 * @author Nicholas
	 * @version 1.0.0
	 */
	private UserDtoOut userDtoOut(User u) {

		UserDtoOut dto = new UserDtoOut();

		Optional.ofNullable(u.getNome()).ifPresent(dto::setName);
		Optional.ofNullable(u.getCognome()).ifPresent(dto::setSurname);
		Optional.ofNullable(u.getEmail()).ifPresent(dto::setEmail);
		Optional.ofNullable(u.getBooksInPossession())
				.map(list -> list.stream().map(bs::libroDtoOut).collect(Collectors.toList()))
				.ifPresent(dto::setBooksInPossession);

		log.info("Utente dto: " + dto.toString());
		return dto;
	}

	/**
	 * Recupera un utente dal sistema e restituisce un oggetto {@code UserDtoOut}
	 * contenente le sue informazioni.
	 *
	 * @param email l'email dell'utente da cercare
	 * @return l'oggetto {@code UserDtoOut} corrispondente all'utente
	 * @throws UserNotFoundException se l'utente non è registrato con l'email
	 *                               fornita
	 * @author Nicholas
	 * @version 1.0.0
	 */
	@Override
	@Cacheable(value = "userCache", key = "#email")
	public UserDtoOut findUser(String email) {

		User user = finder(email);

		log.info("Utente con email: " + email + " trovato");
		return userDtoOut(user);
	}

// ----------------------------------------------------- USERDTOIN --------------------------------------------------------

	/**
	 * Crea un nuovo utente nel sistema e restituisce un oggetto {@code UserDtoIn}
	 * con i dati forniti.
	 * <p>
	 * L'operazione viene eseguita solo se l'email non è già registrata.
	 * </p>
	 *
	 * @param name    il nome dell'utente
	 * @param surname il cognome dell'utente
	 * @param email   l'email dell'utente
	 * @return l'oggetto {@code UserDtoIn} creato
	 * @throws UserAlreadyExistException se l'email è già presente nel sistema
	 *                                   utenti
	 * @author Nicholas
	 * @version 1.0.0
	 */
	@Override
	public UserDtoIn createDtoin(String name, String surname, String email) {
		
		UserDtoIn u = UserDtoIn.builder().name(name).surname(surname).email(email).build();
		createUser(name, surname, email);
		log.info("Utente DTO creato");
		return u;
	}

}
