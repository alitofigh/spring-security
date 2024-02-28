package com.isc.ali.training.training.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isc.ali.training.training.domain.Role;
import com.isc.ali.training.training.domain.User;
import com.isc.ali.training.training.service.UserServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

/**
 * Created by A_Tofigh at 2/14/2024
 */

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class UserResource {
    private final UserServiceImpl userService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @PostMapping("/user/save")
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveUser(user));
    }

    @PostMapping("/role/save")
    public ResponseEntity<Role> saveRole(@RequestBody Role role) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/role/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveRole(role));
    }

    @PostMapping("/user/add-role-to-user")
    public ResponseEntity<?> addRoleToUser(@RequestParam String username, @RequestParam String role) {
        userService.addRoleToUser(username, role);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refresh_token = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                String username = decodedJWT.getSubject();
                User user = userService.getUser(username);
                String access_token =
                        JWT.create()
                                .withSubject(user.getUsername())
                                .withExpiresAt(new java.sql.Date(System.currentTimeMillis() + 10 * 60 * 1000))
                                .withIssuer(request.getRequestURL().toString())
                                .withClaim("roles", user.getRoles().stream().map(Role::getName)
                                        .collect(Collectors.toList()))
                                .sign(algorithm);

                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);

            } catch (Exception e) {
                response.setHeader("error", e.getMessage());
                response.setHeader("code", "" + FORBIDDEN.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                Map<String, String> map = new HashMap<>();
                map.put("error", e.getMessage());
                new ObjectMapper().writeValue(response.getOutputStream(), map);
            }
        } else
            throw new RuntimeException("refresh token is not valid");
    }
}
