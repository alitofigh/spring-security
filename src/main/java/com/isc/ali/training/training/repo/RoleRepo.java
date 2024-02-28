package com.isc.ali.training.training.repo;

import com.isc.ali.training.training.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by A_Tofigh at 2/14/2024
 */

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
