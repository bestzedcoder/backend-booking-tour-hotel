package com.bestzedcoder.project3.booking_tour_hotel;

import com.bestzedcoder.project3.booking_tour_hotel.exception.ResourceNotFoundException;
import com.bestzedcoder.project3.booking_tour_hotel.model.Profile;
import com.bestzedcoder.project3.booking_tour_hotel.model.Role;
import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import com.bestzedcoder.project3.booking_tour_hotel.repository.RoleRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.UserRepository;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableMethodSecurity
@SpringBootApplication
@RequiredArgsConstructor
@EnableAsync
@Slf4j
public class Application implements CommandLineRunner {
	private final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	@Value("${DATABASE_URL}")
	private String databaseURL;

	@Value("${server.port}")
	private String port;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("Database URL: " + databaseURL);
		System.out.println("Server run port: " + port);
		var roles = this.roleRepository.findAll();
		if (roles.isEmpty()) {
			var defaultRoles = List.of(
					new Role("ROLE_CUSTOMER"),
					new Role("ROLE_ADMIN"),
					new Role("ROLE_BUSINESS")
			);
			roleRepository.saveAll(defaultRoles);
		}

		var user = this.userRepository.findByUsername("admin");
		if (user == null) {
			Profile profile = Profile.builder().fullName("admin").build();
			User admin = User.builder()
					.roles(new HashSet<>(this.roleRepository.findAll()))
					.email("admin@gmail.com")
					.username("admin")
					.password(this.passwordEncoder.encode("admin"))
					.enabled(true)
					.profile(profile)
					.updateProfile(true)
					.build();
			profile.setUser(admin);
			this.userRepository.save(admin);
		}

	}
}
