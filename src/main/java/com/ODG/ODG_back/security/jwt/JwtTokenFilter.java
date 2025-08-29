package com.ODG.ODG_back.security.jwt;

import com.ODG.ODG_back.exception.custom.UnauthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String token = jwtTokenProvider.resolveToken(request);
        if (token == null) {
            token = JwtCookieUtil.extractTokenFromCookie(request);
        }

        if (token != null) {
            try {
                jwtTokenProvider.validateToken(token);
                String userId = jwtTokenProvider.getUserId(token);

                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userId, null, List.of());

                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (UnauthorizedException e) {
                SecurityContextHolder.clearContext();
                throw e;
            }
        }
        filterChain.doFilter(request, response);
    }

}
