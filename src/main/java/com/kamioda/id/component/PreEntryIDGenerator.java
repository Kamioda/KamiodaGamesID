package com.kamioda.id.component;

import org.springframework.stereotype.Component;

@Component
public class PreEntryIDGenerator extends RandomString {
    private static String CHARACTERS = "0123456789";
    public PreEntryIDGenerator() {
        super(CHARACTERS);
    }
    public String generate() {
        return "pei-" + super.generate(12);
    }
}
