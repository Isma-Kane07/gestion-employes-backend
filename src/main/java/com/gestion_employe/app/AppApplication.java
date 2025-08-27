package com.gestion_employe.app;

import com.gestion_employe.app.entities.Role;
import com.gestion_employe.app.entities.User;
import com.gestion_employe.app.repositories.RoleRepository;
import com.gestion_employe.app.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@SpringBootApplication
public class AppApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppApplication.class, args);
	}

    @Bean
    public CommandLineRunner initData(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            // Créer les rôles s'ils n'existent pas
            if (roleRepository.findByName("ADMIN") == null) {
                roleRepository.save(new Role(null, "ADMIN"));
            }
            if (roleRepository.findByName("EMPLOYE_CHEF") == null) {
                roleRepository.save(new Role(null, "EMPLOYE_CHEF"));
            }
            if (roleRepository.findByName("EMPLOYE") == null) {
                roleRepository.save(new Role(null, "EMPLOYE"));
            }

            // Créer un utilisateur ADMIN de test s'il n'existe pas
            if (userRepository.findByUsername("admin") == null) {
                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setPassword(passwordEncoder.encode("test1234")); // Mot de passe encodé
                adminUser.setRoles(Collections.singletonList(roleRepository.findByName("ADMIN")));
                userRepository.save(adminUser);
            }
        };
    }

}
