package com.kamioda.id.controller;

import org.springframework.http.ResponseEntity;

import com.kamioda.id.exception.*;

public class ControllerBase {
    protected static ResponseEntity<String> exceptionProcess(Exception e) {
        e.printStackTrace();
        return switch (e) {
            case BadRequestException br -> ResponseEntity.badRequest().body(br.getMessage());
            case UnauthorizationException ua -> ResponseEntity.status(401).body(ua.getMessage());
            case NotFoundException nf -> ResponseEntity.status(404).body(nf.getMessage());
            default -> ResponseEntity.internalServerError().body("Internal Server Error");
        };
    }
    protected static String pickupAccessToken(String authHeader) throws UnauthorizationException {
        if (authHeader == null || authHeader.isEmpty()) throw new UnauthorizationException("AuthHeader must not be empty");
        String[] parts = authHeader.split(" ");
        if (parts.length != 2 || parts[0] != "Bearer") throw new UnauthorizationException("Invalid AuthHeader format");
        return parts[1];
    }
}
