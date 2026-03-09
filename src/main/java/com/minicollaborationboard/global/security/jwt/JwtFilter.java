package com.minicollaborationboard.global.security.jwt;

import com.minicollaborationboard.domain.auth.entity.Role;
import com.minicollaborationboard.domain.auth.entity.User;
import com.minicollaborationboard.global.security.constants.PermitAuthPath;
import com.minicollaborationboard.global.security.dto.JwtClaims;
import com.minicollaborationboard.global.security.dto.CustomUserDetails;
import com.minicollaborationboard.global.security.handler.CustomAuthenticationFailureHandler;
import io.jsonwebtoken.ExpiredJwtException;
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
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        String path = request.getRequestURI();

        return PermitAuthPath.permitAuthPaths.stream()
                .anyMatch(pattern -> antPathMatcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {

            customAuthenticationFailureHandler.onAuthenticationFailure(request, response, new BadCredentialsException("유효하지 않은 토큰입니다."));

            return;
        }

        String token = authorization.split(" ")[1];

        try {

            JwtClaims jwtClaims = jwtTokenProvider.validateToken(token);

            User user = User.builder()
                    .email(jwtClaims.getEmail())
                    .role(Role.valueOf(jwtClaims.getRole()))
                    .password(null)
                    .uuid(jwtClaims.getUuid())
                    .build();

            CustomUserDetails customUserDetails = new CustomUserDetails(user);

            Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (ExpiredJwtException e) {

            customAuthenticationFailureHandler.onAuthenticationFailure(request, response, new BadCredentialsException("만료된 토큰입니다."));

            return;
        } catch (JwtException e) {

            customAuthenticationFailureHandler.onAuthenticationFailure(request, response, new BadCredentialsException("유효하지 않은 토큰입니다."));

            return;
        }

        filterChain.doFilter(request, response);
    }
}
