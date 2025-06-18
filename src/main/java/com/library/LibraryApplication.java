package com.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(
		info = @Info (
				title = "Library TEST",
				version ="1.1.0",
				description ="Questo swagger serve solo a fare testing",
				termsOfService="TOS",
				contact = @Contact(
						name = "Nicholas",
						email = "NOTRealEmail@email.nent"),
				license = @License (
						name = "Nicholas",
						url = "safenot"
						)				
				)
		)

@SpringBootApplication
@EnableCaching
public class LibraryApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryApplication.class, args);
	}

}
