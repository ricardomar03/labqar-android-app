package com.ua.ricardomartins.qualar.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ricardo on 18/05/16.
 */
public class GcmToken {

    @SerializedName("token")
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
