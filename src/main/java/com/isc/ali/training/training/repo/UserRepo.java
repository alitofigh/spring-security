package com.isc.ali.training.training.repo;

import com.isc.ali.training.training.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by A_Tofigh at 2/14/2024
 */

public interface UserRepo extends JpaRepository<User, Long> {
    User findUserByUsername(String username);
}
