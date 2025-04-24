package com.plus.service.user.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plus.service.global.dto.ErrorResponse;
import com.plus.service.global.error.BusinessException;
import com.plus.service.user.application.TokenService;
import com.plus.service.user.presentation.dto.TokenClaimDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TokenFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = request.getHeader("Authorization");
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
            try {
                TokenClaimDto claims = tokenService.getClaimsFromAccessToken(accessToken);
                UserDetails userDetails = userDetailsService.loadUserByUsername(claims.userName());
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }catch (BusinessException e) {
                SecurityContextHolder.clearContext();
                response.setStatus(e.getStatusCode());
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                ErrorResponse errorResponse = ErrorResponse.of(e.getMessage(), e.getStatusCode());
                response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
                return;
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                ErrorResponse errorResponse = ErrorResponse.of("Invalid token", HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
