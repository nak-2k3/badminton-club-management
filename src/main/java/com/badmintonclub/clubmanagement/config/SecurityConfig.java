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

                                                // Public
                                                .requestMatchers(
                                                                "/",
                                                                "/login",
                                                                "/css/**",
                                                                "/js/**",
                                                                "/images/**")
                                                .permitAll()

                                                // Profile: mọi tài khoản đã đăng nhập đều xem được
                                                .requestMatchers(
                                                                "/profile",
                                                                "/profile/edit",
                                                                "/profile/update",
                                                                "/profile/change-password")
                                                .hasAnyRole("ADMIN", "MEMBER", "TREASURER")

                                                // User management chỉ Admin
                                                .requestMatchers("/users/**")
                                                .hasRole("ADMIN")

                                                // Schedule management chỉ Admin
                                                .requestMatchers(
                                                                "/schedules/create",
                                                                "/schedules/save",
                                                                "/schedules/edit/**",
                                                                "/schedules/lock/**",
                                                                "/schedules/open/**",
                                                                "/schedules/cancel/**")
                                                .hasRole("ADMIN")

                                                // Xem lịch + tham gia
                                                .requestMatchers(
                                                                "/schedules",
                                                                "/schedules/register/**",
                                                                "/schedules/cancel-registration/**",
                                                                "/schedules/participants/**")
                                                .hasAnyRole("ADMIN", "MEMBER", "TREASURER")

                                                // Dashboard chỉ Admin
                                                .requestMatchers("/dashboard")
                                                .hasRole("ADMIN")

                                                .requestMatchers("/payments/**")
                                                .hasAnyRole("ADMIN", "TREASURER")

                                                .requestMatchers("/fee-settings/**")
                                                .hasAnyRole("ADMIN", "TREASURER")

                                                .requestMatchers("/my-payments")
                                                .hasAnyRole("ADMIN", "MEMBER", "TREASURER")

                                                .requestMatchers("/expenses/**")
                                                .hasAnyRole("ADMIN", "TREASURER")

                                                .requestMatchers("/reports/**")
                                                .hasAnyRole("ADMIN", "TREASURER")
                                                // Các route còn lại phải đăng nhập
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