package com.example.marisco.myapplication;

public class ListOccurrenceCommentData {

    public LoginResponse token;
    public Long userID;
    public Long occurrenceID;
    public String cursor;


    public ListOccurrenceCommentData() {

    }

    public ListOccurrenceCommentData(LoginResponse token, Long userID,  Long occurrenceID, String cursor) {
        this.token = token;
        this.userID = userID;
        this.occurrenceID = occurrenceID;
        this.cursor = cursor;
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

    public Long getOccurrenceID() {
        return occurrenceID;
    }

    public void setOccurrenceID(Long occurrenceID) {
        this.occurrenceID = occurrenceID;
    }

    public String getCursor() {
        return cursor;
    }

    public void setCursor(String cursor) {
        this.cursor = cursor;
    }
}
