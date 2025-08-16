package com.kamioda.id.service;

import java.io.BufferedWriter;
import java.io.FileWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import com.kamioda.id.component.RandomString;
import com.kamioda.id.model.User;
import com.kamioda.id.model.dto.ApplicationCreateRequest;
import com.kamioda.id.model.dto.ApplicationCreateResponse;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SystemInitializationService implements ApplicationRunner {
    private final UserService userService;
    private ApplicationService applicationService;
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
        ApplicationCreateRequest masterApplication = new ApplicationCreateRequest(
            "アカウントポータル",
            "Kamioda Games IDポータル",
            "https://id.kamioda.tokyo/callback"
        );
        ApplicationCreateResponse res = applicationService.create(masterUser, masterApplication);
        BufferedWriter bw = new BufferedWriter(new FileWriter("./data/initialization.txt"));
        bw.write("Portal Application ID: " + res.getApplicationId() + "\nPortal Application Secret: " + res.getClientSecret());
        bw.close();
    }
}
