package com.isc.ali.training.training.filter;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

/**
 * Created by A_Tofigh at 2/20/2024
 */

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        if (request.getServletPath().equals("/login") || request.getServletPath().equals("/api/token/refresh"))
            filterChain.doFilter(request, response);
        else {
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                try {
                    String token = authorizationHeader.substring("Bearer ".length());
                    Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                    JWTVerifier verifier = JWT.require(algorithm).build();
                    DecodedJWT decodedJWT = verifier.verify(token);
                    String username = decodedJWT.getSubject();
                    String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                    Collection<SimpleGrantedAuthority> grantedAuthority = new ArrayList<>();
                    stream(roles).forEach(role -> grantedAuthority.add(new SimpleGrantedAuthority(role)));
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(username, null, grantedAuthority);
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    filterChain.doFilter(request, response);
                } catch (Exception e) {
                    log.error("Error in logging in {} ", e.getMessage());
                    response.setHeader("error", e.getMessage());
                    response.setHeader("code", "" + FORBIDDEN.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    Map<String, String> map = new HashMap<>();
                    map.put("error", e.getMessage());
                    new ObjectMapper().writeValue(response.getOutputStream(), map);
                }
            } else
                filterChain.doFilter(request, response);
        }
    }
}
