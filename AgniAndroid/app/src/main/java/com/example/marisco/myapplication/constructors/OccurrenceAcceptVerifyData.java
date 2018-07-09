package com.example.marisco.myapplication.constructors;

import com.example.marisco.myapplication.LoginResponse;

public class OccurrenceAcceptVerifyData {

    public LoginResponse token;
    public Long userID;
    public Long occurrenceUserID;

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

    public Long getOccurrenceUserID() {
        return occurrenceUserID;
    }

    public void setOccurrenceUserID(Long occurrenceUserID) {
        this.occurrenceUserID = occurrenceUserID;
    }

    public Long getOccurrenceID() {
        return occurrenceID;
    }

    public void setOccurrenceID(Long occurrenceID) {
        this.occurrenceID = occurrenceID;
    }

    public Long occurrenceID;


    public OccurrenceAcceptVerifyData() {

    }

    public OccurrenceAcceptVerifyData(LoginResponse token, Long userID, Long occurrenceUserID, Long occurrenceID) {
        this.token = token;
        this.userID = userID;
        this.occurrenceUserID = occurrenceUserID;
        this.occurrenceID = occurrenceID;
    }


}
