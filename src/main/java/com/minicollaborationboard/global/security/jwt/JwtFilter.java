package com.minicollaborationboard.global.security.jwt;

import com.minicollaborationboard.domain.user.entity.Role;
import com.minicollaborationboard.domain.user.entity.User;
import com.minicollaborationboard.global.security.dto.ClaimsResDto;
import com.minicollaborationboard.global.security.dto.CustomUserDetails;
import com.minicollaborationboard.global.security.handler.CustomAuthenticationFailureHandler;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    private final List<String> ignorePath = List.of("/login", "/signup");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (ignorePath.contains(path)) {

            filterChain.doFilter(request, response);

            return;
        }

        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {

            customAuthenticationFailureHandler.onAuthenticationFailure(request, response, new BadCredentialsException("유효하지 않은 토큰입니다."));

            return;
        }

        String token = authorization.split(" ")[1];

        try {

            ClaimsResDto claimsResDto = jwtUtil.validateToken(token);

            User user = User.builder()
                    .email(claimsResDto.getUsername())
                    .role(Role.valueOf(claimsResDto.getRole()))
                    .password("temppassword")
                    .uuid(claimsResDto.getUuid())
                    .build();

            CustomUserDetails customUserDetails = new CustomUserDetails(user);

            Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JwtException e) {

            customAuthenticationFailureHandler.onAuthenticationFailure(request, response, new BadCredentialsException("유효하지 않은 토큰입니다."));

            return;
        }

        filterChain.doFilter(request, response);
    }
}
