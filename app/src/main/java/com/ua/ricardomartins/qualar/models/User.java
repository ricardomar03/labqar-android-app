package com.ua.ricardomartins.qualar.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ricardo on 19/04/16.
 */
public class User {
    @SerializedName("id")
    private int id;

    @SerializedName("username")
    private String username;


    @SerializedName("name")
    private String name;

    @SerializedName("superuser")
    private Boolean superuser;

    @SerializedName("token")
    private String token;

    public User(int id, String username, String name, Boolean superuser, String token){
        this.id = id;
        this.username = username;
        this.name = name;
        this.superuser = superuser;
        this.token = token;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean isSuperuser() {
        return superuser;
    }

    public void setSuperuser(Boolean superuser) {
        this.superuser = superuser;
    }

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
}
