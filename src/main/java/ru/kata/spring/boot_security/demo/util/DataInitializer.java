package ru.kata.spring.boot_security.demo.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        if (roleRepository.count() == 0) {
            Role admin = roleRepository.save(new Role("ROLE_ADMIN"));
            Role user = roleRepository.save(new Role("ROLE_USER"));

            User adminUser = new User();
            adminUser.setName("admin");
            adminUser.setSurname("admin");
            adminUser.setAge(35);
            adminUser.setEmail("admin@mail.ru");
            adminUser.setPassword(passwordEncoder.encode("admin"));
            adminUser.setRoles(Set.of(admin, user));
            userRepository.save(adminUser);

            User normalUser = new User();
            normalUser.setName("user");
            normalUser.setSurname("user");
            normalUser.setAge(30);
            normalUser.setEmail("user@mail.ru");
            normalUser.setPassword(passwordEncoder.encode("user"));
            normalUser.setRoles(Set.of(user));
            userRepository.save(normalUser);
        }
    }
}