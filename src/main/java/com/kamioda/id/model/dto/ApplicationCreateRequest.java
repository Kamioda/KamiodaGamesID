package com.kamioda.id.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationCreateRequest {
    @JsonProperty(value = "name", required = true)
    private String name;

    @JsonProperty(value = "description", required = false)
        private String description;

    @JsonProperty(value = "redirect_uri", required = true)
    private String redirectUri;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getRedirectUri() {
        return redirectUri;
    }
}
