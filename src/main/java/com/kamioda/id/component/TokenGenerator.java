package com.kamioda.id.component;

import org.springframework.stereotype.Component;

@Component
public class TokenGenerator extends RandomString {
    private static final String TOKEN_CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    public TokenGenerator() {
        super(TOKEN_CHARACTERS);
    }
    public String generate(int length) {
        return super.generate(length);
    }
}
