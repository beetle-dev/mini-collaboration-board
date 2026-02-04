package com.minicollaborationboard.global.security.jwt;

import com.minicollaborationboard.domain.user.entity.Role;
import com.minicollaborationboard.global.security.dto.CustomUserDetails;
import com.minicollaborationboard.global.security.handler.CustomAuthenticationFailureHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public LoginFilter(AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtTokenProvider,
                       CustomAuthenticationFailureHandler customAuthenticationFailureHandler) {
        this.setAuthenticationManager(authenticationManager);
        this.setAuthenticationFailureHandler(customAuthenticationFailureHandler);
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        String username = obtainUsername(request);
        String password = obtainPassword(request);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password, null);

        return this.getAuthenticationManager().authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();

        String uuid = customUserDetails.getUuid();
        String username = customUserDetails.getUsername();
        String role = authResult.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse(Role.USER.toString());

        String token = jwtTokenProvider.createJwt(uuid, username, role);

        response.setHeader("X-Auth-Token", "Bearer " + token);
    }
}
