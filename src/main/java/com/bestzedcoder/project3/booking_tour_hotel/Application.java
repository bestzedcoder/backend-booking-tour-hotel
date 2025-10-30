package com.bestzedcoder.project3.booking_tour_hotel;

import com.bestzedcoder.project3.booking_tour_hotel.exception.ResourceNotFoundException;
import com.bestzedcoder.project3.booking_tour_hotel.model.Profile;
import com.bestzedcoder.project3.booking_tour_hotel.model.Role;
import com.bestzedcoder.project3.booking_tour_hotel.model.User;
import com.bestzedcoder.project3.booking_tour_hotel.repository.RoleRepository;
import com.bestzedcoder.project3.booking_tour_hotel.repository.UserRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableMethodSecurity
@SpringBootApplication
@RequiredArgsConstructor
public class Application implements CommandLineRunner {
	private final RoleRepository roleRepository;
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
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

		var user = this.userRepository.findByUsername("admin");
		if (user == null) {
			Profile profile = Profile.builder().fullName("admin").build();
			User admin = User.builder().roles(Set.of(this.roleRepository.findByName("ROLE_ADMIN").orElseThrow(() -> new ResourceNotFoundException(
          "Role admin not found")))).email("admin@gmail.com").username("admin").password(this.passwordEncoder.encode("admin")).enabled(true).profile(profile).updateProfile(true).build();
			profile.setUser(admin);
			this.userRepository.save(admin);
		}

	}
}
