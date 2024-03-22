package com.youcode.test.services.impl;

import com.youcode.test.exceptions.ResourceNotFoundException;
import com.youcode.test.models.dto.AuthRequestDTO;
import com.youcode.test.models.dto.AuthResponseDTO;
import com.youcode.test.models.dto.UserDTO;
import com.youcode.test.models.entities.User;
import com.youcode.test.repositories.UserRepository;
import com.youcode.test.security.JWTService;
import com.youcode.test.services.UserService;
import com.youcode.test.utils.SecurityHelpers;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
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
    public Iterable<User> save(List<User> users) {
        return userRepository.saveAll(users);
    }

}
