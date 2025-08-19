package com.kamioda.id.component;

import java.util.Base64;

import com.kamioda.id.exception.UnauthorizationException;

public final class AppAuthorization {
    private static final Base64.Decoder base64Decoder = Base64.getDecoder();
    private final String clientId;
    private final String clientSecret;
    public AppAuthorization(String authHeader) {
        String[] authValues = authHeader.split(" ");
        if (authValues[0] != "Basic") throw new UnauthorizationException("client authorization error");
        String decoded = new String(base64Decoder.decode(authValues[1]));
        String[] credentials = decoded.split(":");
        if (credentials.length != 2) throw new UnauthorizationException("client authorization error");
        this.clientId = credentials[0];
        this.clientSecret = credentials[1];
    }
    public String getClientId() {
        return clientId;
    }
    public String getClientSecret() {
        return clientSecret;
    }

}
