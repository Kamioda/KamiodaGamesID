package com.kamioda.id.component;

import java.util.Base64;
import java.util.HexFormat;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public final class HashUtils {
    private static final Map<String, HashComponent> components = Map.of(
        "sha1", new HashComponent("SHA-1"),
        "sha256", new HashComponent("SHA-256"),
        "sha512", new HashComponent("SHA-512"),
        "md5", new HashComponent("MD5")
    );
    public static String toHash(String input, String algorithm, String format, int stretchCount) {
        if (algorithm.equalsIgnoreCase("s256")) return HashUtils.toHash(input, "sha256", format, stretchCount);
        HashComponent component = components.get(algorithm.toLowerCase());
        Base64.Encoder encoder = Base64.getEncoder();
        if (component == null) {
            if (algorithm.equalsIgnoreCase("plain")) return format.equals("base64") ? encoder.encodeToString(input.getBytes()) : input;
            else throw new IllegalArgumentException("Invalid hashing algorithm");
        } else {
            byte[] hashed = input.getBytes();
            for (int i = 0; i < stretchCount; i++) {
                hashed = component.toHash(hashed);
            }
            switch(format.toLowerCase()) {
                case "hex":
                    return HexFormat.of().formatHex(hashed).toUpperCase();
                case "base64":
                    return encoder.encodeToString(hashed);
                default:
                    throw new IllegalArgumentException("Invalid format");
            }
        }
    }
    public static String toHash(String input, String algorithm, String format) {
        return toHash(input, algorithm, format, 1);
    }
    public static String toHash(String input, String algorithm, int stretchCount) {
        return toHash(input, algorithm, "hex", stretchCount);
    }
    public static String toHash(String input, String algorithm) {
        return toHash(input, algorithm, "hex", 1);
    }
}
