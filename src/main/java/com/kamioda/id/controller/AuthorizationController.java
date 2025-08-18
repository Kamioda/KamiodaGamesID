package com.kamioda.id.controller;

import java.net.URI;
import java.util.Map;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kamioda.id.service.AuthorizationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController(value = "oauth")
public class AuthorizationController extends ControllerBase {
    @Autowired
    private AuthorizationService authorizationService;
    @GetMapping("/oauth/authorize")
    public ResponseEntity<?> startAuthorization(
        @RequestParam("response_type") String responseType,
        @RequestParam("client_id") String clientId, 
        @RequestParam("redirect_uri") String redirectUri,
        @RequestParam("code_challenge") String codeChallenge,
        @RequestParam("code_challenge_method") String codeChallengeMethod
    ) {
        try {
            if (responseType != "code") throw new BadRequestException("Invalid response type");
            if (clientId == null || redirectUri == null || codeChallenge == null || codeChallengeMethod == null) {
                throw new BadRequestException("Missing required parameters");
            }
            String authId = authorizationService.createAuthorization(clientId, redirectUri, codeChallenge, codeChallengeMethod);
            return ResponseEntity.status(302).location(new URI("https://id.kamioda.tokyo/authorize?auth_id=" + authId)).build();
        } catch (Exception e) {
            return exceptionProcess(e);
        }
    }
    @PostMapping("/oauth/authorize/{authId}")
    public ResponseEntity<?> authorize(@PathVariable String authId, @RequestBody Map<String, String> body) {
        try {
            String redirectUri = authorizationService.authorizationWithIDAndPassword(authId, body.get("id"), body.get("password"));
            return ResponseEntity.status(302).location(URI.create(redirectUri)).build();
        } catch (Exception e) {
            return exceptionProcess(e);
        }
    }
    @PostMapping("/oauth/token")
    public ResponseEntity<?> issueToken(@RequestBody Map<String, String> body) {
        try {
            switch(body.get("grant_type")) {
                case "authorization_code":
                    return ResponseEntity.ok(authorizationService.issueToken(
                        body.get("auth_code"),
                        body.get("code_verifier"),
                        body.get("redirect_uri")
                    ));
                case "refresh_token":
                    return ResponseEntity.ok(authorizationService.issueToken(
                        body.get("refresh_token")
                    ));
                default:
                    throw new BadRequestException("Invalid grant type");
            }
        } catch (Exception e) {
            return exceptionProcess(e);
        }
    }
}
