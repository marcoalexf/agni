package com.example.marisco.myapplication;

public class ListOccurrenceLikeData {

    public LoginResponse token;
    public Long userID;
    public String cursor;

    public ListOccurrenceLikeData() {

    }

    public ListOccurrenceLikeData(LoginResponse token, Long userID, String cursor) {
        this.token = token;
        this.userID = userID;
        this.cursor = cursor;
    }

    public boolean valid() {
        return userID != null && token != null;
    }

    public LoginResponse getToken() {
        return token;
    }

    public void setToken(LoginResponse token) {
        this.token = token;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }
}

