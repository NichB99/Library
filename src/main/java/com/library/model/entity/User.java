package com.library.model.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String cognome;
    private String email;
    @JsonManagedReference
    @OneToMany(mappedBy = "proprietario", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Book> booksInPossession;
    
    public boolean canBorrowMoreBooks() {
        return this.booksInPossession.size() >= 5;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "name='" + nome + '\'' +
                ", surname='" + cognome + '\'' +
                ", email='" + email + '\'' +
                ", booksInPossession=" + (booksInPossession != null ? booksInPossession.size() + " books" : "null") +
                '}';
    }
    
}
