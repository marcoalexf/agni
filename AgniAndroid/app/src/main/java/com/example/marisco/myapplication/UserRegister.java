package com.example.marisco.myapplication;

import com.google.gson.annotations.SerializedName;

public class UserRegister extends User {

    @SerializedName("user_type")
    private String user_type;

    @SerializedName("email")
    private String email;

    public UserRegister(String username, String password, String email, String user_type){
        super(username, password);
        this.email = email;
        this.user_type = user_type;
    }

    public String getUser_type() {
        return user_type;
    }

    public String getEmail() {
        return email;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
