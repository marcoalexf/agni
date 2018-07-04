package com.example.marisco.myapplication;

public class OccurrenceLikeCountData {

    public Long userID;
    public Long occurrenceID;


    public OccurrenceLikeCountData() {

    }

    public OccurrenceLikeCountData(LoginResponse token, Long userID,  Long occurrenceID) {
        this.userID = userID;
        this.occurrenceID = occurrenceID;
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