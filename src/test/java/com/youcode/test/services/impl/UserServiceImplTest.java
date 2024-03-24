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
import com.youcode.test.utils.SecurityHelpers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private SecurityHelpers securityHelpers;
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
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        given(securityContext.getAuthentication()).willReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
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
    @Test
    @DisplayName("Test loadUserByUsername method when the user is not found")
    public void testLoadUserByUsernameNotFound() {
        String username = "non_existing_user";
        given(userRepository.findByUsername(username)).willReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(username));
        verify(userRepository).findByUsername(username);
    }

    @Test
    @DisplayName("Test login method with incorrect password")
    public void testLoginWithIncorrectPassword() {
        AuthRequestDTO authRequest = new AuthRequestDTO("john_doe", "incorrect_password");
        given(userRepository.findByUsername(authRequest.getUsername())).willReturn(Optional.of(user));
        assertThrows(InsufficientAuthenticationException.class, () -> userService.login(authRequest));
        verify(userRepository).findByUsername(authRequest.getUsername());
    }

    @Test
    @DisplayName("Test login method with non-existing username")
    public void testLoginWithNonExistingUsername() {
        AuthRequestDTO authRequest = new AuthRequestDTO("non_existing_user", "password123");
        given(userRepository.findByUsername(authRequest.getUsername())).willReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.login(authRequest));
        verify(userRepository).findByUsername(authRequest.getUsername());
    }

    @Test
    @DisplayName("Test getProfile method when user does not exist")
    public void testGetProfileUserNotFound() {
        String username = "non_existing_user";
        given(userRepository.findByUsername(username)).willReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getProfile(username));
    }

    @Test
    @DisplayName("Test getProfile method when user exists")
    public void testGetProfileUserExists() {
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(user));
        given(modelMapper.map(user, UserDTO.class)).willReturn(userDTO);
        UserDTO result = userService.getProfile("john_doe");
       assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Test getAuthenticatedProfile method")
    public void testGetAuthenticatedProfile() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("john_doe");
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        given(userRepository.findByUsername("john_doe")).willReturn(Optional.of(user));
        given(modelMapper.map(user, UserDTO.class)).willReturn(userDTO);
        UserDTO result = userService.getAuthenticatedProfile();
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Test getAuthenticatedProfile method when user does not exist")
    public void testGetAuthenticatedProfileUserNotFound() {
        String username = "non_existing_user";
        when(SecurityHelpers.retrieveUsername()).thenReturn(username);
        given(userRepository.findByUsername(username)).willReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getAuthenticatedProfile());
    }

    @Test
    @DisplayName("Test batchInsertUsers method")
    public void testBatchInsertUsers() throws IOException {
        String jsonData = "[{\"username\":\"user1\", \"password\":\"pass1\"}, {\"username\":\"user2\", \"password\":\"pass2\"}]";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(jsonData.getBytes());
        List<User> users = Arrays.asList(
                User.builder().username("user1").password("pass1").build(),
                User.builder().username("user2").password("pass2").build()
        );
        when(objectMapper.readValue(any(InputStream.class), any(TypeReference.class))).thenReturn(users);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        BatchInsertionResponseDTO response = userService.batchInsertUsers(inputStream);
        assertEquals(2, response.getSuccessfullyInsertedRows());
        assertEquals(0, response.getFailedToInsertRows());
        verify(userRepository, times(2)).save(any(User.class));
    }


}
