package com.kamioda.id.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import com.kamioda.id.model.User;
import com.kamioda.id.model.dto.ApplicationCreateRequest;
import com.kamioda.id.model.dto.ApplicationCreateResponse;
import com.kamioda.id.model.dto.TokenDTO;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SystemInitializationService implements ApplicationRunner {
    @Autowired
    private UserService userService;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private AuthorizationService authorizationService;
    @Autowired
    private TokenService tokenService;
    @Override
    @Transactional(readOnly = false)
    public void run(ApplicationArguments args) throws Exception {
        if (userService.getCurrentAccountCount() > 0) return;
        User masterUser = new User(
            "0000000000000",
            "localsystem",
            "Local System",
            "kamioda@mail.kamioda.tokyo",
            "PNjqPEBIW3ERwr2b"
        );
        userService.create(masterUser);
        Optional<User> developer = userService.findById("0000000000000");
        ApplicationCreateRequest masterApplication = new ApplicationCreateRequest(
            "アカウントポータル",
            "Kamioda Games IDポータル",
            "https://id.kamioda.tokyo/callback"
        );
        ApplicationCreateResponse res = applicationService.create(developer.get(), masterApplication);
        BufferedWriter bw = new BufferedWriter(new FileWriter("./data/initialization.txt"));
        bw.write("Portal Application ID: " + res.getApplicationId() + "\nPortal Application Secret: " + res.getClientSecret());
        bw.close();
        RunCheck(res.getApplicationId());
    }
    private void RunCheck(String appId) {
        String authId = authorizationService.createAuthorization(appId, "https://id.kamioda.tokyo/callback", "test", "plain");
        String redirectUri = authorizationService.authorizationWithIDAndPassword(authId, "localsystem", "PNjqPEBIW3ERwr2b");
        String authCode = redirectUri.split("auth_code=")[1];
        TokenDTO token = authorizationService.issueToken(authCode, "test", "https://id.kamioda.tokyo/callback");
        tokenService.revokeToken(authCode);
    }
}
