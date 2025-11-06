package ru.kata.spring.boot_security.demo.model;

import lombok.Data;

import java.util.Set;

@Data
public class UserDTO {
    private String name;
    private String surname;
    private int age;
    private String email;
    private String password;
    private Set<Long> roles; // здесь будут роли
}
