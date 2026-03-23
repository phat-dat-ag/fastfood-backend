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
        config.setAllowedOrigins(Arrays.asList("http://localhost:5173", "https://fastfood-frontend-eight.vercel.app"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

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
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api-docs/**",
                                "/v3/api-docs/**"
                        ).permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/stripe/webhook").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/image/**").permitAll()
                        .requestMatchers("/api/admin/category/display/**").permitAll()
                        .requestMatchers("/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/review/**").permitAll()
                        .requestMatchers("/api/user/manage/**").hasRole("ADMIN")
                        .requestMatchers("/api/review/manage/**").hasRole("ADMIN")
                        .requestMatchers("/api/quiz/manage/**").hasRole("ADMIN")
                        .requestMatchers("/api/carts/**").hasAnyRole("USER", "STAFF")
                        .requestMatchers(HttpMethod.GET, "/api/orders/**").permitAll()
                        .requestMatchers("/api/orders/**").hasAnyRole("USER", "STAFF")
                        .requestMatchers("/api/review/**").hasAnyRole("USER", "STAFF")
                        .requestMatchers("/api/addresses/**").hasAnyRole("USER", "STAFF")
                        .requestMatchers("/api/topic/display/**").hasAnyRole("USER", "STAFF")
                        .requestMatchers("/api/topic/**").hasRole("ADMIN")
                        .requestMatchers("/api/topic-difficulty/**").hasRole("ADMIN")
                        .requestMatchers("/api/awards/**").hasRole("ADMIN")
                        .requestMatchers("/api/question/**").hasRole("ADMIN")
                        .requestMatchers("/api/image/**").hasRole("ADMIN")
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "STAFF")
                        .requestMatchers("/api/quiz/**").hasAnyRole("USER", "STAFF")
                        .requestMatchers("/api/staff/**").hasRole("STAFF")
                        .requestMatchers("/api/dashboard/**").hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
