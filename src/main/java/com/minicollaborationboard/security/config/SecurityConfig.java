package com.minicollaborationboard.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minicollaborationboard.security.constants.PermitAuthPath;
import com.minicollaborationboard.security.handler.CustomAuthenticationFailureHandler;
import com.minicollaborationboard.security.jwt.JwtFilter;
import com.minicollaborationboard.security.jwt.JwtTokenProvider;
import com.minicollaborationboard.security.jwt.LoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final ObjectMapper objectMapper;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(PermitAuthPath.permitAuthPaths.toArray(new String[0])).permitAll()
                        .anyRequest().authenticated())

                .addFilterBefore(jwtFilter, LoginFilter.class)
                .addFilterAt(new LoginFilter(
                        authenticationConfiguration.getAuthenticationManager(),
                        jwtTokenProvider,
                        customAuthenticationFailureHandler,
                        objectMapper),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
