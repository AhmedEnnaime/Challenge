package com.youcode.test.models.dto;

import com.youcode.test.models.enums.ROLE;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotNull(message = "Birth date is required")
    private Date birthDate;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Avatar URL is required")
    private String avatar;

    @NotBlank(message = "Company name is required")
    private String company;

    @NotBlank(message = "Job position is required")
    private String jobPosition;

    @NotBlank(message = "Mobile number is required")
    private String mobile;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 10, message = "Password should be between 6 and 10 characters")
    private String password;

    @NotNull(message = "Role is required")
    private ROLE role;

}
