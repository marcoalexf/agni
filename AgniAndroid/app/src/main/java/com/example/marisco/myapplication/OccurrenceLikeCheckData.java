package com.example.marisco.myapplication;

public class OccurrenceLikeCheckData {

    public Long userID;
    public Long userOccurrenceID;
    public Long occurrenceID;
    public LoginResponse token;


    public OccurrenceLikeCheckData() {

    }

    public OccurrenceLikeCheckData(LoginResponse token, Long userID, Long userOccurrenceID,  Long occurrenceID) {
        this.token = token;
        this.userID = userID;
        this.userOccurrenceID = userOccurrenceID;
        this.occurrenceID = occurrenceID;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public Long getUserOccurrenceID() {
        return userOccurrenceID;
    }

    public void setUserOccurrenceID(Long userOccurrenceID) {
        this.userOccurrenceID = userOccurrenceID;
    }

    public Long getOccurrenceID() {
        return occurrenceID;
    }

    public void setOccurrenceID(Long occurrenceID) {
        this.occurrenceID = occurrenceID;
    }

    public LoginResponse getToken() {
        return token;
    }

    public void setToken(LoginResponse token) {
        this.token = token;
    }
}
