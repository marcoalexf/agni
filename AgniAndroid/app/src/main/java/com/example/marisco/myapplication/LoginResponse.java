package com.example.marisco.myapplication;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LoginResponse implements Serializable {

    @SerializedName("username")
    String username;

    @SerializedName("userID")
    String userID;

    @SerializedName("tokenID")
    String tokenID;

    @SerializedName("expirationData")
    long expirationDate;

    @SerializedName("creationData")
    long creationDate;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTokenID() {
        return tokenID;
    }

    public void setTokenID(String tokenID) {
        this.tokenID = tokenID;
    }

    public long getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(long expirationDate) {
        this.expirationDate = expirationDate;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public void setUserid(String userid){
        this.userID = userid;
    }

    public String getUserid(){
        return this.userID;
    }
}
