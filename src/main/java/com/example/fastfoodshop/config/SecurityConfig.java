package com.example.fastfoodshop.config;

import com.example.fastfoodshop.security.JwtAccessDeniedHandler;
import com.example.fastfoodshop.security.JwtAuthenticationEntryPoint;
import com.example.fastfoodshop.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    private final JwtAccessDeniedHandler accessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> {
                })
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/stripe/webhook").permitAll()
                        .requestMatchers("/api/admin/category/display/**").permitAll()
                        .requestMatchers("/api/admin/product/display/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/review/**").permitAll()
                        .requestMatchers("/api/order/staff/**").hasRole("STAFF")
                        .requestMatchers("/api/order/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/user/manage/**").hasRole("ADMIN")
                        .requestMatchers("/api/review/manage/**").hasRole("ADMIN")
                        .requestMatchers("/api/cart/**").hasAnyRole("USER", "STAFF")
                        .requestMatchers(HttpMethod.GET, "/api/admin/promotion/order/valid").hasAnyRole("USER", "STAFF")
                        .requestMatchers("/api/order/**").hasAnyRole("USER", "STAFF")
                        .requestMatchers("/api/review/**").hasAnyRole("USER", "STAFF")
                        .requestMatchers("/api/address/**").hasAnyRole("USER", "STAFF")
                        .requestMatchers("/api/topic/display/**").hasAnyRole("USER", "STAFF")
                        .requestMatchers("/api/topic/**").hasRole("ADMIN")
                        .requestMatchers("/api/topic-difficulty/**").hasRole("ADMIN")
                        .requestMatchers("/api/award/**").hasRole("ADMIN")
                        .requestMatchers("/api/question/**").hasRole("ADMIN")
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "STAFF")
                        .requestMatchers("/api/quiz/**").hasAnyRole("USER", "STAFF", "ADMIN")
                        .requestMatchers("/api/staff/**").hasRole("STAFF")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
