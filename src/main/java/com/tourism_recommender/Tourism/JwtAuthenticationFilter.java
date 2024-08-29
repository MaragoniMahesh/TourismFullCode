package com.tourism_recommender.Tourism;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Skip JWT validation for permitted endpoints
        if (path.equals("/api/users/register") || path.equals("/api/users/login") || path.equals("/api/users/check-email") ||
                path.equals("/api/users/send-otp") || path.equals("/api/users/verify-otp") || path.equals("/api/users/change-password") ||
                path.equals("/api/users/post/search") || path.equals("/api/users/post") || path.equals("/api/users/post/image/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Continue with JWT validation for other endpoints
        String token = getJwtFromRequest(request);
        if (token != null) {
            try {
                Claims claims = jwtTokenProvider.validateToken(token);
                if (claims != null) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(claims);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
                    return;
                }
            } catch (ExpiredJwtException e) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Token Expired");
                return;
            } catch (JwtException e) {
                sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
                return;
            }
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "No Token Provided");
            return;
        }

        filterChain.doFilter(request, response);
    }


    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }

    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"error\": \"%s\"}", message));
        response.getWriter().flush(); // Ensure the response is sent
    }
}
