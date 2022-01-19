package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override public List<User> getAllUsers() { return userRepository.findAll(); }

    @Override public User getUserById(long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override public void saveUser(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    @Override public void updateUser(User user) {

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            User existing = getUserById(user.getId());
            if (existing != null) user.setPassword(existing.getPassword());
        } else {
            user.setPassword(encoder.encode(user.getPassword()));
        }
        userRepository.save(user);
    }

    @Override public void deleteUser(long id) { userRepository.deleteById(id); }

    @Override public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public void saveOrUpdate(User user) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            User existing = getUserById(user.getId());
            if (existing != null) user.setPassword(existing.getPassword());
        } else {
            user.setPassword(encoder.encode(user.getPassword()));
        }
        userRepository.save(user);
    }
}