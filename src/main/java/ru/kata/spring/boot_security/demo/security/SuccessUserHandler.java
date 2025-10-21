package ru.kata.spring.boot_security.demo.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Component
public class SuccessUserHandler implements AuthenticationSuccessHandler {

    private static final Map<String, String> ROLE_REDIRECT_MAP = Map.of(
            "ROLE_ADMIN", "/admin",
            "ROLE_USER", "/user"
    );

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        log.info("User {} logged in with roles {}", authentication.getName(), roles);

        String redirectUrl = roles.stream()
                .map(ROLE_REDIRECT_MAP::get)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("/");

        if (redirectUrl.equals("/")) {
            log.warn("No redirect found for roles: {}", roles);
        }

        response.sendRedirect(redirectUrl);
    }
}