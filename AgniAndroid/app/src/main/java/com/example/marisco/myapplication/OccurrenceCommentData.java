package com.example.marisco.myapplication;

public class OccurrenceCommentData {


    public LoginResponse token;
    public Long userID;
    public Long occurrenceID;
    public String comment;


    public OccurrenceCommentData() {

    }

    public OccurrenceCommentData(LoginResponse token, Long userID,  Long occurrenceID, String comment) {
        this.token = token;
        this.userID = userID;
        this.occurrenceID = occurrenceID;
        this.comment = comment;
    }



}