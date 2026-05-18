package com.workhub.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final TenantFilter tenantFilter;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    public SecurityConfig(TenantFilter tenantFilter, JwtAuthenticationEntryPoint authenticationEntryPoint) {
        this.tenantFilter = tenantFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(authenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                AntPathRequestMatcher.antMatcher("/auth/login"),
                                AntPathRequestMatcher.antMatcher("/actuator/health/**"),
                                AntPathRequestMatcher.antMatcher("/actuator/info"),
                                AntPathRequestMatcher.antMatcher("/actuator/metrics/**"),
                                AntPathRequestMatcher.antMatcher("/actuator/prometheus")
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/projects").hasRole("TENANT_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/projects/*/tasks").hasRole("TENANT_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/projects/*/generate-report").authenticated()
                        .requestMatchers(HttpMethod.POST, "/tasks").hasRole("TENANT_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users").hasRole("TENANT_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/users").hasRole("TENANT_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/projects/**").hasRole("TENANT_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/tasks/**").hasRole("TENANT_ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(tenantFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
