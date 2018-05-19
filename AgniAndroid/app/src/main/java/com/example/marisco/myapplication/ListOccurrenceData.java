package com.example.marisco.myapplication;

import com.google.gson.annotations.SerializedName;

public class ListOccurrenceData {

    @SerializedName("token") LoginResponse token;
    @SerializedName("showPrivate") boolean showPrivate;
    @SerializedName("username") String username;


    public ListOccurrenceData(LoginResponse token, boolean showPrivate, String username) {
        this.token = token;
        this.showPrivate = showPrivate;
        this.username = username;
    }


    public LoginResponse getToken() {
        return token;
    }

    public void setToken(LoginResponse token) {
        this.token = token;
    }

    public boolean isShowPrivate() {
        return showPrivate;
    }

    public void setShowPrivate(boolean showPrivate) {
        this.showPrivate = showPrivate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
