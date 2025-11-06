package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    void add(User user);
    void update(Long id, User user);
    void delete(Long id);
    User get(Long id);
    List<User> getAllUsers();
    void assignRoles(User user, Set<Long> roleIds);
    User findByEmail(String email);
}