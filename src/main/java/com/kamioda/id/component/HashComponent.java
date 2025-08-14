package com.kamioda.id.component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashComponent {
    private MessageDigest messageDigest;
    public HashComponent(String method) {
        try {
            this.messageDigest = MessageDigest.getInstance(method);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Invalid hashing algorithm", e);
        }
    }
    public byte[] toHash(byte[] input) {
        return messageDigest.digest(input);
    }
}
