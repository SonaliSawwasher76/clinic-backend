package com.clinic.config;

import com.clinic.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    // Constructor to inject JwtUtil
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // Override the abstract method doFilterInternal
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);

        // If the token is present and valid, proceed with the authentication
        if (token != null) {
            // Extract username from token
            String username = jwtUtil.extractUsername(token);

            if (jwtUtil.isTokenValid(token, username)) {
                Claims claims = jwtUtil.extractAllClaims(token);

                // Create an authentication token and set it in the security context
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, null); // Provide authorities if available

                // Set the authentication token in the SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }

    // Helper method to extract token from the request
    private String extractToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7); // Remove "Bearer " prefix
        }
        return null; // No token found
    }
}
