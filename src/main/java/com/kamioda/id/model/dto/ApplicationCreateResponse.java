package com.kamioda.id.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class ApplicationCreateResponse {
    @JsonProperty(value = "client_id")
    private final String appId;
    @JsonProperty(value = "client_secret")
    private final String appSecret;
    public ApplicationCreateResponse(String appId, String appSecret) {
        this.appId = appId;
        this.appSecret = appSecret;
    }
}
