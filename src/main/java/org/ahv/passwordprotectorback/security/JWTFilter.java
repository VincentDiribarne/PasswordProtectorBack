package org.ahv.passwordprotectorback.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.ahv.passwordprotectorback.model.User;
import org.ahv.passwordprotectorback.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getHeader("Authorization") == null) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getToken(request);
        Claims claims = jwtService.extractAllClaims(token, false);

        User user = userService.findByUsername(claims.getSubject());

        if (user != null) {
            Authentication authentication = new UsernamePasswordAuthenticationToken(claims.getSubject(), null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        String token = null;

        if (request.getHeader("Authorization") != null) {
            token = request.getHeader("Authorization").substring(7);
        }

        return token;
    }
}
