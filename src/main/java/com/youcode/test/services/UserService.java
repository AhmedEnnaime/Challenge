package com.youcode.test.services;

import com.youcode.test.models.dto.AuthRequestDTO;
import com.youcode.test.models.dto.AuthResponseDTO;
import com.youcode.test.models.dto.UserDTO;
import com.youcode.test.models.entities.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    User findByUsername(String username);
    AuthResponseDTO login(AuthRequestDTO login);
    UserDTO getAuthenticatedProfile();
    UserDTO getProfile(String username);
    Iterable<User> save(List<User> users);
}
