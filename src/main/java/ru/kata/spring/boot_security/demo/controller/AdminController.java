package ru.kata.spring.boot_security.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    @GetMapping
    public String showAllUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "admin/list";
    }

    @GetMapping("/users/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "admin/form";
    }

    @PostMapping("/users")
    public String createUser(@ModelAttribute User user, @RequestParam(required = false) List<Long> roles) {
        Set<Long> roleIds = roles == null ? Set.of() : new HashSet<>(roles);
        userService.assignRoles(user, roleIds);
        userService.add(user);
        return "redirect:/admin";
    }

    @GetMapping("/users/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        User user = userService.get(id);
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.getAllRoles());
        return "admin/edit";
    }

    @PostMapping("/users/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute User user,
                             @RequestParam(required = false) List<Long> roles) {
        Set<Long> roleIds = roles == null ? Set.of() : new HashSet<>(roles);
        user.setRoles(new HashSet<>()); // avoid stale roles
        userService.assignRoles(user, roleIds);
        userService.update(id, user);
        return "redirect:/admin";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id, Model model) {
        // Optionally add confirmation logic
        userService.delete(id);
        return "redirect:/admin";
    }
}