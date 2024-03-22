package com.youcode.test.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youcode.test.exceptions.ResourceNotFoundException;
import com.youcode.test.models.dto.AuthRequestDTO;
import com.youcode.test.models.dto.AuthResponseDTO;
import com.youcode.test.models.dto.BatchInsertionResponseDTO;
import com.youcode.test.models.dto.UserDTO;
import com.youcode.test.models.entities.User;
import com.youcode.test.models.enums.ROLE;
import com.youcode.test.repositories.UserRepository;
import com.youcode.test.security.JWTService;
import com.youcode.test.services.UserService;
import com.youcode.test.utils.SecurityHelpers;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final ObjectMapper objectMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.debug("Entering in loadUserByUsername Method...");
        User user = findByUsername(username);
        if (!user.isEnabled()) {
            throw new DisabledException("User account is not enabled");
        }
        log.info("User Authenticated Successfully..!!!");
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("could not found user..!!"));
    }

    @Override
    public AuthResponseDTO login(AuthRequestDTO login) {
        var user = loadUserByUsername(login.getUsername());
        if(passwordEncoder.matches(login.getPassword(), user.getPassword())){
            String token = jwtService.GenerateToken(user);
            String username = user.getUsername();
            User userData = userRepository.findByUsername(username).get();
            return AuthResponseDTO.builder().accessToken(token).role(userData.getRole().name()).build();
        }
        throw new InsufficientAuthenticationException("unauthorized");
    }

    @Override
    public UserDTO getAuthenticatedProfile() {
        String username = SecurityHelpers.retrieveUsername();
        User retrievedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username " + username + " not found"));
        return mapper.map(retrievedUser, UserDTO.class);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @Override
    public UserDTO getProfile(String username) {
        User retrievedUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username " + username + " not found"));
        return mapper.map(retrievedUser, UserDTO.class);
    }

    @Override
    public BatchInsertionResponseDTO batchInsertUsers(InputStream inputStream) {
        TypeReference<List<User>> typeReference = new TypeReference<List<User>>() {};

        try {
            List<User> users = objectMapper.readValue(inputStream, typeReference);
            int successfullyInserted = 0;
            int failedToInsert = 0;

            for (User user : users) {
                try {
                    userRepository.save(user);
                    successfullyInserted++;
                } catch (Exception e) {
                    failedToInsert++;
                    e.printStackTrace();
                }
            }
            return new BatchInsertionResponseDTO(successfullyInserted, failedToInsert);
        } catch (IOException e) {
            e.printStackTrace();
            return new BatchInsertionResponseDTO(0, -1);
        }
    }

    @Override
    public String generateRandomUserData(int count) {
        List<User> users = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            User user = User.builder()
                    .firstName(RandomStringUtils.randomAlphabetic(8))
                    .lastName(RandomStringUtils.randomAlphabetic(8))
                    .birthDate(new Date()
                    )
                    .city(RandomStringUtils.randomAlphabetic(8))
                    .country(RandomStringUtils.randomAlphabetic(8))
                    .avatar("avatar_url_" + i)
                    .company(RandomStringUtils.randomAlphabetic(10))
                    .jobPosition(RandomStringUtils.randomAlphabetic(10))
                    .mobile(RandomStringUtils.randomNumeric(10))
                    .username("user" + i)
                    .email("user" + i + "@example.com")
                    .password(RandomStringUtils.randomAlphanumeric(10))
                    .role(ROLE.values()[random.nextInt(ROLE.values().length)])
                    .build();
            users.add(user);
        }

        try {
            return objectMapper.writeValueAsString(users);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

}
