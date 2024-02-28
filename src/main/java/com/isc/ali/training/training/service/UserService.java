package com.isc.ali.training.training.service;

import com.isc.ali.training.training.domain.Role;
import com.isc.ali.training.training.domain.User;

import java.util.List;

/**
 * Created by A_Tofigh at 2/14/2024
 */

public interface UserService {
    User saveUser(User user);
    Role saveRole(Role role);
    void addRoleToUser(String username, String role);
    User getUser(String username);
    List<User> getUsers();
}
