package ru.kata.spring.boot_security.demo.service;

import javax.persistence.*;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void add(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void update(Long id, User updatedUser) {
        User existing = get(id);
        if (existing != null) {
            existing.setName(updatedUser.getName());
            existing.setSurname(updatedUser.getSurname());
            existing.setAge(updatedUser.getAge());

            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
                existing.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            existing.setRoles(updatedUser.getRoles());
            userRepository.save(existing);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User get(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional
    public void assignRoles(User user, Set<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            user.setRoles(new HashSet<>());
            return;
        }
        Set<Role> roles = roleIds.stream()
                .map(id -> roleRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Role not found: " + id)))
                .collect(Collectors.toSet());
        user.setRoles(roles);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }
}