package com.team25.event.planner.user.controller;

import com.team25.event.planner.common.exception.ServerError;
import com.team25.event.planner.user.dto.BlockRequestDTO;
import com.team25.event.planner.user.dto.UserRequestDTO;
import com.team25.event.planner.user.dto.UserResponseDTO;
import com.team25.event.planner.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UserRequestDTO userRequestDTO
    ) {
        return ResponseEntity.ok(userService.updateUser(userId, userRequestDTO));
    }

    @PostMapping("/{userId}/block")
    public ResponseEntity<Void> blockUser(
            @PathVariable Long userId,
            @Valid @RequestBody BlockRequestDTO blockRequestDTO
    ) {
        userService.blockUser(userId, blockRequestDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{accountId}/suspend")
    public ResponseEntity<Void> suspendUser(
            @PathVariable Long accountId
    ) {
        userService.suspendUser(accountId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{userId}/profile-picture")
    public ResponseEntity<Resource> getProfilePicture(@PathVariable Long userId) {
        try {
            Resource resource = userService.getProfilePicture(userId);

            // Determine the content type (e.g., image/jpeg)
            String contentType = Files.probeContentType(Path.of(resource.getFile().getAbsolutePath()));
            if (contentType == null) {
                contentType = "application/octet-stream"; // Fallback to binary stream
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (IOException e) {
            throw new ServerError("Could not load image", 500);
        }
    }

    @GetMapping("/{userId}/pictures/{pictureId}")
    public ResponseEntity<Resource> getOwnerPicture(@PathVariable Long userId, @PathVariable String pictureId) {
        try {
            Resource resource = userService.getOwnerPicture(userId, pictureId);

            // Determine the content type (e.g., image/jpeg)
            String contentType = Files.probeContentType(Path.of(resource.getFile().getAbsolutePath()));
            if (contentType == null) {
                contentType = "application/octet-stream"; // Fallback to binary stream
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (IOException e) {
            throw new ServerError("Could not load image", 500);
        }
    }
}
