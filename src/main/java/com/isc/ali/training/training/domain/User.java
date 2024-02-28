package com.isc.ali.training.training.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 * Created by A_Tofigh at 2/14/2024
 */

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String username;
    private String password;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;
}
