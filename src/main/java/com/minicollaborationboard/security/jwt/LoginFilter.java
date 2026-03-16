package com.minicollaborationboard.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.minicollaborationboard.security.dto.CustomUserDetails;
import com.minicollaborationboard.security.handler.CustomAuthenticationFailureHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json; charset=UTF-8");

        LoginResDto body = new LoginResDto("Bearer", token);
        objectMapper.writeValue(response.getWriter(), body);
    }

    public record LoginResDto (String tokenType, String token){}
}
