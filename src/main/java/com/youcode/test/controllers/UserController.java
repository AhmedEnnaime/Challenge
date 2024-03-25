package com.youcode.test.controllers;

import com.youcode.test.models.dto.BatchInsertionResponseDTO;
import com.youcode.test.models.dto.UserDTO;
import com.youcode.test.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(path = "api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping(path = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BatchInsertionResponseDTO> batchInsertUsers(@RequestPart("file") MultipartFile file) {
        try {
            BatchInsertionResponseDTO response = userService.batchInsertUsers(file.getInputStream());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BatchInsertionResponseDTO(0, -1));
        }
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('USER', 'ADMIN')")
    public ResponseEntity<UserDTO> getMe() {
        UserDTO me = userService.getAuthenticatedProfile();
        return ResponseEntity.ok(me);
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserDTO> getProfile(@PathVariable String username) {
        UserDTO user = userService.getProfile(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/generate")
    public ResponseEntity<byte[]> generateRandomUsers(@RequestParam("count") int count) {
        String jsonUserData = userService.generateRandomUserData(count);
        byte[] data;
        try {
            data = jsonUserData.getBytes("UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        String timestamp = Long.toString(System.currentTimeMillis());
        String filename = "random_users_" + timestamp + ".json";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData(filename, filename);
        return ResponseEntity.ok()
                .headers(headers)
                .body(data);
    }
}
