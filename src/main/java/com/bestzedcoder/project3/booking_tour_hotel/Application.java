package com.bestzedcoder.project3.booking_tour_hotel;

import com.bestzedcoder.project3.booking_tour_hotel.model.Role;
import com.bestzedcoder.project3.booking_tour_hotel.repository.RoleRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableJpaAuditing
@EnableMethodSecurity
@SpringBootApplication
@RequiredArgsConstructor
public class Application implements CommandLineRunner {
	private final RoleRepository roleRepository;

	@Value("${DATABASE_URL}")
	private String databaseURL;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Database URL: " + databaseURL);
		var roles = this.roleRepository.findAll();
		if (roles.isEmpty()) {
			var defaultRoles = List.of(
					new Role("ROLE_CUSTOMER"),
					new Role("ROLE_ADMIN"),
					new Role("ROLE_BUSINESS")
			);
			roleRepository.saveAll(defaultRoles);
		}

	}
}
