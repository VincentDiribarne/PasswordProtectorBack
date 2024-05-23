package org.ahv.passwordprotectorback.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.ahv.passwordprotectorback.model.Token;
import org.ahv.passwordprotectorback.model.TokenType;
import org.ahv.passwordprotectorback.model.User;
import org.ahv.passwordprotectorback.repository.TokenRepository;
import org.ahv.passwordprotectorback.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JWTService jwtService;
    private final UserService userService;
    private final TokenRepository tokenRepository;
    private String token = null;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            filterChain.doFilter(request, response);
            return;
        }

        token = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("token")).findFirst().map(Cookie::getValue).orElse(null);

        if (token != null) {
            Claims claims = jwtService.extractAllClaims(token, false);

            User user = userService.findByUsername(claims.getSubject());
            Date expirationDate = claims.getExpiration();

            if (user != null) {
                List<Token> tokens = tokenRepository.findAllByUserID(user.getId());
                Token presentToken = tokens.stream()
                        .filter(t -> t.getToken().equals(token) && !t.isAlreadyUsed() && t.getExpirationDate().equals(expirationDate) && t.getType().equals(TokenType.TOKEN))
                        .findFirst()
                        .orElse(null);

                if (presentToken != null && !presentToken.isAlreadyUsed()) {
                    if (new Date().before(expirationDate)) {
                        Authentication authentication = new UsernamePasswordAuthenticationToken(claims.getSubject(), null, null);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        } else {
            filterChain.doFilter(request, response);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
