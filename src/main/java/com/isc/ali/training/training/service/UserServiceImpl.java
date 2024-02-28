package com.isc.ali.training.training.service;

import com.isc.ali.training.training.domain.Role;
import com.isc.ali.training.training.domain.User;
import com.isc.ali.training.training.repo.RoleRepo;
import com.isc.ali.training.training.repo.UserRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by A_Tofigh at 2/14/2024
 */

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("user not found {}", username));
        }
        Collection<GrantedAuthority> authority = new ArrayList<>();
        user.getRoles().forEach(auth -> {
            authority.add(new SimpleGrantedAuthority(auth.getName()));
        });
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authority);
    }

    @Override
    public User saveUser(User user) {
        log.info("saving new user {} to database ", user.getName());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepo.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        log.info("saving new role {} to database", role.getName());
        return roleRepo.save(role);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        log.info("adding role {} to user {}", roleName, username);
        User user = userRepo.findUserByUsername(username);
        Role role = roleRepo.findByName(roleName);
        user.getRoles().add(role);
    }

    @Override
    public User getUser(String username) {
        log.info("fetching user {} from database", username);
        return userRepo.findUserByUsername(username);
    }

    @Override
    public List<User> getUsers() {
        log.info("fetching all users from database");
        return userRepo.findAll();
    }
}
