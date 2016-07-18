package com.ua.ricardomartins.qualar.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ricardo on 23/04/16.
 */
public class NewSettings {
    @SerializedName("name_changed")
    private Boolean name_changed;

    @SerializedName("name")
    private String name;

    @SerializedName("username_changed")
    private Boolean username_changed;

    @SerializedName("username")
    private String username;

    @SerializedName("password_changed")
    private Boolean password_changed;

    /**
     * @return The dateTime
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username The date_time
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return The values
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The values
     */
    public void setName(String name) {
        this.name = name;
    }

    public Boolean didUsernameChanged() {
        return username_changed;
    }

    public void setUsername_changed(Boolean username_changed) {
        this.username_changed = username_changed;
    }

    public Boolean didNameChanged() {
        return name_changed;
    }

    public void setName_changed(Boolean name_changed) {
        this.name_changed = name_changed;
    }

    public Boolean didPasswordChanged() {
        return password_changed;
    }

    public void setPassword_changed(Boolean password_changed) {
        this.password_changed = password_changed;
    }
}
