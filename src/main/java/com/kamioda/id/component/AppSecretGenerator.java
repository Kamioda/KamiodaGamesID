package com.kamioda.id.component;

import org.springframework.stereotype.Component;

@Component
public class AppSecretGenerator extends RandomString {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public AppSecretGenerator() {
        super(CHARACTERS);
    }
    private static final int APP_SECRET_LENGTH = 64;
    public String generate() {
        return super.generate(APP_SECRET_LENGTH);
    }
}
