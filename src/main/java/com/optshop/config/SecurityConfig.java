package com.optshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.optshop.security.JwtFilter;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig 
{

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
            	    
            	    .requestMatchers("/auth/**").permitAll()
            	    .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
            	    .requestMatchers("/orders/success/**").permitAll()
            	    .requestMatchers("/orders/cancel").permitAll()
            	    
            	   
            	    .requestMatchers(HttpMethod.POST, "/products/**").hasRole("ADMIN")
            	    .requestMatchers(HttpMethod.PUT, "/products/**").hasRole("ADMIN")
            	    .requestMatchers(HttpMethod.DELETE, "/products/**").hasRole("ADMIN")
            	    
            	   
            	    .anyRequest().authenticated()
            	)
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}