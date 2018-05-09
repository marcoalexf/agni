package com.example.marisco.myapplication;

import com.google.gson.annotations.SerializedName;

public class UserRegister extends User {

    @SerializedName("name")
    private String name;

    @SerializedName("role")
    private String role;

    @SerializedName("email")
    private String email;

    public UserRegister(String name, String username, String password, String email, String role){
        super(username, password);
        this.email = email;
        this.role = role;
    }

    public String getUser_type() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public void setUser_type(String role) {
        this.role = role;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
