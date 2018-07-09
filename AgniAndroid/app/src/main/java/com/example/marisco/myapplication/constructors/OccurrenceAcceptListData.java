package com.example.marisco.myapplication.constructors;

import com.example.marisco.myapplication.LoginResponse;

public class OccurrenceAcceptListData {

    public LoginResponse token;
    public Long userID;
    public String cursor;


    public OccurrenceAcceptListData() {

    }

    public OccurrenceAcceptListData(LoginResponse token, Long userID, String cursor) {
        this.token = token;
        this.userID = userID;
        this.cursor = cursor;
    }

}
