package com.badmintonclub.clubmanagement.config;

import com.badmintonclub.clubmanagement.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {

        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/",
                                "/css/**",
                                "/js/**")
                        .permitAll()

                        .requestMatchers("/users/**")
                        .hasRole("ADMIN")
                        // .requestMatchers(
                        // "/users/create",
                        // "/users/save")
                        // .permitAll()

                        // .requestMatchers("/users/**")
                        // .hasRole("ADMIN")
                        .anyRequest()
                        .authenticated())

                .formLogin(form -> form

                        .loginPage("/login")
                        .defaultSuccessUrl("/")
                        .permitAll())

                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout"))

                .userDetailsService(customUserDetailsService);

        return http.build();
    }
}