package com.isc.ali.training.training;

import com.isc.ali.training.training.domain.Role;
import com.isc.ali.training.training.domain.User;
import com.isc.ali.training.training.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;

@SpringBootApplication(/*exclude={DataSourceAutoConfiguration.class}*/)
public class AliTrainingApplication {

	public static void main(String[] args) {
		SpringApplication.run(AliTrainingApplication.class, args);
	}



//	@Bean
//	BCryptPasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder();
//	}

	// by this method we can test functionality of our application
	@Bean
	CommandLineRunner run(UserService userService) {
		return args -> {
			userService.saveUser(new User(null, "john smith", "john", "123", new ArrayList<>()));

			userService.saveRole(new Role(null, "ROLE_ADMIN"));

			userService.addRoleToUser("john", "ROLE_ADMIN");
		};
	}

}
