package com.pruebaapipok.pruebaapipok.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.pruebaapipok.pruebaapipok.filter.JwtRequestFilter;
import com.pruebaapipok.pruebaapipok.service.MyUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfigurer {

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Este es el AuthenticationManager preferido y ya usa tu UserDetailsService y PasswordEncoder
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // DaoAuthenticationProvider se usa internamente por el AuthenticationManager
    // No es necesario exponerlo como un bean separado a menos que lo necesites para personalización avanzada
    // Pero tenerlo aquí para referencia no es un problema
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(myUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Deshabilitar CSRF para APIs REST sin estado
            .authorizeHttpRequests(authz -> authz
                // Endpoints que no requieren autenticación (permitAll)
                .requestMatchers("/authenticate", "/register").permitAll()
                // Consola H2 (solo para desarrollo)
                .requestMatchers("/h2-console/**").permitAll()
                // Endpoint para precargar datos (si es público)
                .requestMatchers("/api/items/preload").permitAll()
                // Todas las demás solicitudes requieren autenticación
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Deshabilitar sesiones de Spring Security (API REST)
            )
            .headers(headers -> headers.frameOptions().disable()) // Necesario para mostrar la consola H2 en un iframe
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class); // Añadir el filtro JWT antes del filtro de autenticación estándar

        return http.build();
    }
}