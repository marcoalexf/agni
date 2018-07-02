package com.example.marisco.myapplication;


public class OccurrenceEditData {

    public LoginResponse token;
    public Long userID;
    public Long occurrenceID;
    public String title;
    public String description;
    public Boolean visibility;
    public Boolean notificationOnResolve;
    public Boolean uploadMedia;
    public int nUploads;

    public OccurrenceEditData() {

    }

    public OccurrenceEditData(LoginResponse token, Long userID, Long occurrenceID, String title,
                              String description,  Boolean visibility, Boolean notificationOnResolve,
                              Boolean uploadMedia, int nUploads) {
        this.token = token;
        this.userID = userID;
        this.occurrenceID = occurrenceID;
        this.title = title;
        this.description = description;
        this.visibility = visibility;
        this.notificationOnResolve = notificationOnResolve;
        this.uploadMedia = uploadMedia;
        this.nUploads = nUploads;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getVisibility() {
        return visibility;
    }

    public void setVisibility(Boolean visibility) {
        this.visibility = visibility;
    }

    public Boolean getNotificationOnResolve() {
        return notificationOnResolve;
    }

    public void setNotificationOnResolve(Boolean notificationOnResolve) {
        this.notificationOnResolve = notificationOnResolve;
    }

    public Boolean getUploadMedia() {
        return uploadMedia;
    }

    public void setUploadMedia(Boolean uploadMedia) {
        this.uploadMedia = uploadMedia;
    }

    public int getnUploads() {
        return nUploads;
    }

    public void setnUploads(int nUploads) {
        this.nUploads = nUploads;
    }
}
