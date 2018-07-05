package com.example.marisco.myapplication.constructors;

import com.example.marisco.myapplication.LoginResponse;
import com.google.gson.annotations.SerializedName;

public class ProfileRequest {

    @SerializedName("username")
    String username;

    @SerializedName("token")
    LoginResponse token;

    public ProfileRequest(String username, LoginResponse token){
        this.username = username;
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LoginResponse getToken() {
        return token;
    }

    public void setToken(LoginResponse token) {
        this.token = token;
    }
}
