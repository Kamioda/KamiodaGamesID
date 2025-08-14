package com.kamioda.id.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.USE_DEFAULTS)
public class ApplicationInformation {
    @JsonProperty("client_id")
    private String appId;
    @JsonProperty("name")
    private String appName;
    @JsonProperty("description")
    private String appDescription;
    @JsonProperty("redirect_uri")
    private String redirectUri;
    @JsonProperty("developer")
    private String developer;
    public ApplicationInformation(String appId, String appName, String appDescription, String redirectUri, String developer) {
        this.appId = appId;
        this.appName = appName;
        this.appDescription = appDescription;
        this.redirectUri = redirectUri;
        this.developer = developer;
    }
}
