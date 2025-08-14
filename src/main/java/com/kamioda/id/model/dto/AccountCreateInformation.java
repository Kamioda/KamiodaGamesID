package com.kamioda.id.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class AccountCreateInformation {
    @JsonProperty("user_id")
    private String userId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("password")
    private String password;
    public String getUserId() {
        return userId;
    }
    public String getName() {
        return name;
    }
    public String getPassword() {
        return password;
    }
}
