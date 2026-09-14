package com.flowpay.security.config;

import jakarta.servlet.DispatcherType;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.MapPropertySource;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityConfigTests {
    @Test
    void publicEndpointsProtectedEndpointsAndErrorDispatch() throws Exception {
        try (var context = new AnnotationConfigWebApplicationContext()) {
            context.setServletContext(new MockServletContext());
            context.getEnvironment().getPropertySources().addFirst(new MapPropertySource("test", Map.of(
                    "security.jwt.secret", "test-secret",
                    "security.jwt.issuer", "flowpay",
                    "security.jwt.access-token-expiration", "15m",
                    "security.jwt.refresh-token-expiration", "7d")));
            context.register(TestConfig.class);
            context.refresh();
            MockMvc mvc = MockMvcBuilders.webAppContextSetup(context)
                    .addFilters(context.getBean("springSecurityFilterChain", jakarta.servlet.Filter.class))
                    .build();

            mvc.perform(post("/api/v1/auth/login")).andExpect(status().isOk());
            mvc.perform(post("/api/v1/auth/register")).andExpect(status().isOk());
            mvc.perform(post("/api/v1/wallets")).andExpect(status().isUnauthorized());
            mvc.perform(post("/api/v1/wallets").header("Authorization", "Bearer USER"))
                    .andExpect(status().isOk());
            mvc.perform(post("/api/v1/admin/users").header("Authorization", "Bearer USER"))
                    .andExpect(status().isForbidden());
            mvc.perform(post("/api/v1/admin/users").header("Authorization", "Bearer ADMIN"))
                    .andExpect(status().isOk());
            mvc.perform(post("/error").with(request -> {
                request.setDispatcherType(DispatcherType.ERROR);
                return request;
            })).andExpect(status().isOk());
            mvc.perform(post("/error")).andExpect(status().isUnauthorized());
            mvc.perform(post("/api/v1/auth/login").header("Authorization", "Bearer invalid"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Configuration
    @EnableWebSecurity
    @EnableWebMvc
    @Import(SecurityConfig.class)
    static class TestConfig {
        @Bean
        JwtDecoder jwtDecoder() {
            return token -> {
                if (!List.of("USER", "ADMIN").contains(token)) {
                    throw new BadJwtException("Invalid test token");
                }
                return Jwt.withTokenValue(token).header("alg", "HS256")
                        .subject("1").claim("roles", List.of(token)).build();
            };
        }

        @Bean
        TestController testController() {
            return new TestController();
        }
    }

    @RestController
    static class TestController {
        @RequestMapping({"/api/v1/auth/login", "/api/v1/auth/register", "/api/v1/wallets",
                "/api/v1/admin/users", "/error"})
        String endpoint() {
            return "ok";
        }
    }
}
