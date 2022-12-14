package ru.hogwarts.schoolfive;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition
public class SchoolFiveApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchoolFiveApplication.class, args);
	}

}
