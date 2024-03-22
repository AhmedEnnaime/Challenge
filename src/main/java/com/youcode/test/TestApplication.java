package com.youcode.test;

import com.youcode.test.models.entities.User;
import com.youcode.test.models.enums.ROLE;
import com.youcode.test.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Date;

@SpringBootApplication
public class TestApplication {

    private PasswordEncoder passwordEncoder;
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    @Bean
    public CommandLineRunner myCommandLineRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            User user = User.builder()
                    .firstName("Ahmed")
                    .lastName("Ennaime")
                    .birthDate(new Date())
                    .city("City")
                    .country("Country")
                    .avatar("avatar_url")
                    .company("Company")
                    .jobPosition("Manager")
                    .mobile("123456789")
                    .username("AhmedEnnaime")
                    .email("ahmed@gmail.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(ROLE.ADMIN)
                    .build();

            userRepository.save(user);
            User user2 = User.builder()
                    .firstName("John")
                    .lastName("Doe")
                    .birthDate(new Date())
                    .city("New York")
                    .country("USA")
                    .avatar("another_avatar_url")
                    .company("Another Company")
                    .jobPosition("Developer")
                    .mobile("987654321")
                    .username("JohnDoe")
                    .email("john.doe@example.com")
                    .password(passwordEncoder.encode("password123"))
                    .role(ROLE.USER)
                    .build();

            userRepository.save(user2);
            System.out.println("Executing code during application startup.");
        };
    }

}
