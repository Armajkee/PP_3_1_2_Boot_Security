package ru.kata.spring.boot_security.demo.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public DataInitializer(RoleRepository roleRepo, UserRepository userRepo) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        if (roleRepo.findByName("ROLE_ADMIN") == null) {
            roleRepo.save(new Role("ROLE_ADMIN"));
        }
        if (roleRepo.findByName("ROLE_USER") == null) {
            roleRepo.save(new Role("ROLE_USER"));
        }

        if (userRepo.findByUsername("admin").isEmpty()) {
            Role adminRole = roleRepo.findByName("ROLE_ADMIN");
            Role userRole = roleRepo.findByName("ROLE_USER");
            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            roles.add(userRole);
            User admin = new User("admin", encoder.encode("admin"), "Admin Name", "admin@example.com");
            admin.setRoles(roles);
            userRepo.save(admin);
        }

        if (userRepo.findByUsername("user").isEmpty()) {
            Role userRole = roleRepo.findByName("ROLE_USER");
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            User user = new User("user", encoder.encode("user"), "User Name", "user@example.com");
            user.setRoles(roles);
            userRepo.save(user);
        }
    }
}