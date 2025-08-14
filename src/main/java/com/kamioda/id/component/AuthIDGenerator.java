package com.kamioda.id.component;

import org.springframework.stereotype.Component;

@Component
public class AuthIDGenerator extends RandomString {
    public AuthIDGenerator() {
        super("0123456789");
    }
    public String generate() {
        return super.generate(8);
    }
}
