package com.kamioda.id.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.kamioda.id.model.dto.ApplicationCreateRequest;
import com.kamioda.id.model.dto.ApplicationCreateResponse;
import com.kamioda.id.model.dto.ApplicationUpdateRequest;
import com.kamioda.id.service.ApplicationService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController(value = "application")
public class ApplicationController extends ControllerBase {
    @Autowired
    private ApplicationService applicationService;
    @PostMapping("/api/v1/application")
    public ResponseEntity<?> create(@RequestHeader("Authorization") String authHeader, @RequestBody ApplicationCreateRequest requestBody) {
        try {
            String accessToken = pickupAccessToken(authHeader);
            return ResponseEntity.ok(applicationService.create(accessToken, requestBody));
        } catch (Exception e) {
            return exceptionProcess(e);
        }
    }
    @GetMapping("/api/v1/application")
    public ResponseEntity<?> listupApplications(@RequestHeader("Authorization") String authHeader) {
        try {
            String accessToken = pickupAccessToken(authHeader);
            return ResponseEntity.ok(applicationService.getAppByDeveloper(accessToken));
        } catch (Exception e) {
            return exceptionProcess(e);
        }
    }
    @GetMapping("/api/v1/application/{appId}")
    public ResponseEntity<?> getApplication(@PathVariable String appId) {
        try {
            return ResponseEntity.ok(applicationService.getApp(appId));
        } catch (Exception e) {
            return exceptionProcess(e);
        }
    }
    @PatchMapping("/api/v1/application/{appId}")
    public ResponseEntity<?> updateApplication(@PathVariable String appId, @RequestHeader("Authorization") String authHeader, @RequestBody ApplicationUpdateRequest requestBody) {
        try {
            String accessToken = pickupAccessToken(authHeader);
            ApplicationCreateResponse updateResult = applicationService.update(accessToken, appId, requestBody);
            return updateResult == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(updateResult);
        } catch (Exception e) {
            return exceptionProcess(e);
        }
    }
    @DeleteMapping("/api/v1/application/{appId}")
    public ResponseEntity<?> deleteApplication(@PathVariable String appId, @RequestHeader("Authorization") String authHeader) {
        try {
            String accessToken = pickupAccessToken(authHeader);
            applicationService.delete(accessToken, appId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return exceptionProcess(e);
        }
    }
}
