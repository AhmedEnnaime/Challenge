package com.youcode.test.services.impl;

import com.youcode.test.exceptions.ResourceNotFoundException;
import com.youcode.test.models.dto.UserDTO;
import com.youcode.test.models.entities.User;
import com.youcode.test.models.enums.ROLE;
import com.youcode.test.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private UserServiceImpl userService;
    private User user;
    private UserDTO userDTO;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .birthDate(new Date())
                .city("New York")
                .country("USA")
                .avatar("avatar_url")
                .company("ABC Company")
                .jobPosition("Software Engineer")
                .mobile("1234567890")
                .username("john_doe")
                .email("john.doe@example.com")
                .password("password123")
                .role(ROLE.USER)
                .build();
        userDTO = UserDTO.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Smith")
                .birthDate(new Date())
                .city("Los Angeles")
                .country("USA")
                .avatar("avatar_url")
                .company("XYZ Corp.")
                .jobPosition("Data Scientist")
                .mobile("9876543210")
                .username("jane_smith")
                .email("jane.smith@example.com")
                .password("password456")
                .role(ROLE.ADMIN)
                .build();
    }

    @Test
    @DisplayName("Test findByUsername method when the username is valid")
    public void testFindByUsernameSuccess() {
        String username = "john_doe";
        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        User foundUser = userService.findByUsername(username);
        verify(userRepository).findByUsername(username);
        assertThat(foundUser).isNotNull();
    }

    @Test
    @DisplayName("Test findByUsername method when the username is not valid")
    public void testFindByUsername() {
        String username = "jjjjjj";
        given(userRepository.findByUsername(username)).willReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.findByUsername(username));
        verify(userRepository).findByUsername(username);
    }

    @Test
    @DisplayName("Test loadUserByUsername method when the user is enabled")
    public void testLoadUserByUsernameSuccess() {
        String username = "john_doe";
        given(userRepository.findByUsername(username)).willReturn(Optional.of(user));
        UserDetails userDetails = userService.loadUserByUsername(username);
        verify(userRepository).findByUsername(username);
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(user.getUsername());
        assertThat(userDetails.getPassword()).isEqualTo(user.getPassword());
    }

}
