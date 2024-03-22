package com.youcode.test.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youcode.test.models.dto.UserDTO;
import com.youcode.test.models.entities.User;
import com.youcode.test.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@RestController
@RequestMapping(path = "api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @PostMapping(path = "/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Integer>> batchInsertUsers(@RequestPart("file") MultipartFile file) {
        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<User>> typeReference = new TypeReference<List<User>>() {};

        try {
            InputStream inputStream = file.getInputStream();
            List<User> users = mapper.readValue(inputStream, typeReference);
            int successfullyInserted = 0;
            int failedToInsert = 0;

            for (User user : users) {
                try {
                    userService.save(Collections.singletonList(user));
                    successfullyInserted++;
                } catch (Exception e) {
                    failedToInsert++;
                    e.printStackTrace();
                }
            }

            Map<String, Integer> response = new HashMap<>();
            response.put("successfully_inserted", successfullyInserted);
            response.put("failed_to_insert", failedToInsert);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", -1));
        }
    }



    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMe() {
        UserDTO me = userService.getAuthenticatedProfile();
        return ResponseEntity.ok(me);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getProfile(@PathVariable String username) {
        UserDTO user = userService.getProfile(username);
        return ResponseEntity.ok(user);
    }
}
