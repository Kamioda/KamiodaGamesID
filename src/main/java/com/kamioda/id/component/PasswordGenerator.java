package com.kamioda.id.component;

public class PasswordGenerator extends RandomString {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[]{}|;:,.<>?";
    private static final int length = 16;

    public PasswordGenerator() {
        super(CHARACTERS);
    }

    public String generate() {
        return super.generate(length);
    }
}
