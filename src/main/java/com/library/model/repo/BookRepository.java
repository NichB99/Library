package com.library.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.library.model.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

	public Book findByTitoloAndIdentificativoIsbn (String name, String isbnId);
	public Book findByIdentificativoIsbn (String isbnId);
}
