package com.ecommerce.user_service.filter;

import com.ecommerce.user_service.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String authHeader = request.getHeader("Authorization");

        // Skip JWT validation for open endpoints
        if (requestURI.equals("/auth/login") || requestURI.equals("/auth/signup")) {
            chain.doFilter(request, response);
            return;
        }

        // Validate JWT for protected endpoints
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String email = jwtUtil.extractEmail(token);
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    if (jwtUtil.validateToken(token)) {
                        var userDetails = new User(email, "", Collections.emptyList());
                        var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\": \"Invalid or expired JWT token\"}");
                return;
            }
        } else {
            // Block requests without a valid token
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Authorization header is missing or invalid\"}");
            return;
        }

        chain.doFilter(request, response);
    }
}