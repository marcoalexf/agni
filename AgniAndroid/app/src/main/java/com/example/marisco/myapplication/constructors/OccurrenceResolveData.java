package com.example.marisco.myapplication.constructors;

import com.example.marisco.myapplication.LoginResponse;

public class OccurrenceResolveData {

    public LoginResponse token;

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

    public boolean isUploadMedia() {
        return uploadMedia;
    }

    public void setUploadMedia(boolean uploadMedia) {
        this.uploadMedia = uploadMedia;
    }

    public int getnUploads() {
        return nUploads;
    }

    public void setnUploads(int nUploads) {
        this.nUploads = nUploads;
    }

    public Long userID;
    public Long occurrenceID;
    public boolean uploadMedia;
    public int nUploads;


    public OccurrenceResolveData() {

    }

    public OccurrenceResolveData(LoginResponse token, Long userID,  Long occurrenceID, boolean uploadMedia, int nUploads) {
        this.token = token;
        this.userID = userID;
        this.occurrenceID = occurrenceID;
        this.uploadMedia = uploadMedia;
        this.nUploads = nUploads;
    }

    public boolean valid() {
        return userID != null && occurrenceID != null && token != null;
    }

}
