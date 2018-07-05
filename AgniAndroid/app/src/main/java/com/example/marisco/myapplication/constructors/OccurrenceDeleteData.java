package com.example.marisco.myapplication.constructors;

import com.example.marisco.myapplication.LoginResponse;

public class OccurrenceDeleteData {

    public LoginResponse token;
    public Long userID;
    public Long occurrenceID;


    public OccurrenceDeleteData() {

    }

    public OccurrenceDeleteData(LoginResponse token, Long userID,  Long occurrenceID) {
        this.token = token;
        this.userID = userID;
        this.occurrenceID = occurrenceID;
    }

    public boolean valid() {
        return userID != null && occurrenceID != null;
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


}
