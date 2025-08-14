package com.kamioda.id.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationUpdateRequest {
    @JsonProperty(value = "name")
    private String name;

    @JsonProperty(value = "description")
    private String description;

    @JsonProperty(value = "redirect_uri")
    private String redirectUri;

    @JsonProperty(value ="regenerate_client_secret", required = true)
    private Boolean regenerateClientSecret;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public Boolean getRegenerateClientSecret() {
        return regenerateClientSecret;
    }
}
