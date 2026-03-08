package com.minicollaborationboard.global.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minicollaborationboard.global.security.dto.CustomUserDetails;
import com.minicollaborationboard.global.security.handler.CustomAuthenticationFailureHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    public LoginFilter(AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider,
                       CustomAuthenticationFailureHandler customAuthenticationFailureHandler,
                       ObjectMapper objectMapper) {
        this.setAuthenticationManager(authenticationManager);
        this.setAuthenticationFailureHandler(customAuthenticationFailureHandler);
        this.jwtTokenProvider = jwtTokenProvider;
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String username = obtainUsername(request);
        String password = obtainPassword(request);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password, null);

        return this.getAuthenticationManager().authenticate(authenticationToken);
    }

    // LoginFilter.java
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult) throws IOException, ServletException {

        CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();

        String uuid = customUserDetails.getUuid();
        String username = customUserDetails.getUsername();
        String role = customUserDetails.getUser().getRole().toString();

        String token = jwtTokenProvider.createJwt(uuid, username, role);

        // Filter이므로 ResponseEntity 대신 response에 직접 작성
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json; charset=UTF-8");

        LoginResDto body = new LoginResDto("Bearer", token);
        objectMapper.writeValue(response.getWriter(), body);
    }

    public record LoginResDto (String tokenType, String token){}
}
