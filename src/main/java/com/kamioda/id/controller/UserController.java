package com.kamioda.id.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import com.kamioda.id.model.dto.AccountCreateInformation;
import com.kamioda.id.model.dto.AccountUpdateInformation;
import com.kamioda.id.service.UserService;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;

@RestController(value = "user")
public class UserController extends ControllerBase{
    @Autowired
    private UserService userService;
    @PostMapping("/api/v1/user/new")
    public ResponseEntity<?> preEntry(@RequestBody String email) {
        try {
            userService.preEntry(email);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return exceptionProcess(e);
        }
    }
    @PostMapping("/api/v1/user/new/{preEntryId}")
    public ResponseEntity<?> entry(@PathVariable String preEntryId, @RequestBody AccountCreateInformation accountCreateInformation) {
        try {
            userService.entry(preEntryId, accountCreateInformation.getUserId(), accountCreateInformation.getName(), accountCreateInformation.getPassword());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return exceptionProcess(e);
        }
    }
    @GetMapping("/api/v1/user")
    public ResponseEntity<?> getUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String accessToken = pickupAccessToken(authHeader);
            return ResponseEntity.ok(userService.getAccountInformation(accessToken));
        } catch (Exception e) {
            return exceptionProcess(e);
        }
    }
    @PatchMapping("/api/v1/user")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String authHeader, @RequestBody AccountUpdateInformation accountUpdateInformation) {
        try {
            String accessToken = pickupAccessToken(authHeader);
            userService.updateAccountInformation(accessToken, accountUpdateInformation);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return exceptionProcess(e);
        }
    }
    @PatchMapping("/api/v1/user/email/{confirmId}")
    public ResponseEntity<?> updateEmail(@PathVariable String confirmId) {
        try {
            userService.completeEmailUpdate(confirmId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return exceptionProcess(e);
        }
    }
    @DeleteMapping("/api/v1/user")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String accessToken = pickupAccessToken(authHeader);
            userService.deleteAccount(accessToken);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return exceptionProcess(e);
        }
    }
    @DeleteMapping("/api/v1/user/{userId}")
    public ResponseEntity<?> deleteUserForce(
        @RequestHeader("Authorization") String authHeader, 
        @PathVariable String userId,
        @RequestBody String deleteReason
    ) {
        try {
            if (deleteReason == null || deleteReason.isEmpty()) return ResponseEntity.badRequest().body("Delete reason is required");
            String accessToken = pickupAccessToken(authHeader);
            userService.deleteAccount(accessToken, userId, deleteReason);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return exceptionProcess(e);
        }
    }
}
