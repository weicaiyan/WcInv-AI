package com.wcinv.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LixingerTokenResponse {

    @JsonProperty("token")
    private String token;

    public LixingerTokenResponse() {
    }

    public LixingerTokenResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
