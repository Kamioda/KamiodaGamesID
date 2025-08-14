package com.kamioda.id.component;

import java.util.Random;

public class RandomString {
    private static final Random random = new Random();
    private final String characters;
    public RandomString(String characters) {
        this.characters = characters;
    }
    protected String generate(int length) {
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            result.append(characters.charAt(random.nextInt(characters.length())));
        }
        return result.toString();
    }
}
