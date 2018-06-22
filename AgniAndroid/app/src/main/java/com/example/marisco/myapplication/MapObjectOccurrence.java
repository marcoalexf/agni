package com.example.marisco.myapplication;

import java.io.Serializable;
import java.util.ArrayList;

public class MapObjectOccurrence implements Serializable{

    private double user_occurrence_lat, user_occurrence_lon;
    private int user_occurrence_level;
    private boolean user_occurrence_visibility, user_occurrence_notification_on_resolve;
    private String user_occurrence_type, user_occurrence_description, user_occurrence_title, user_occurrence_date;
    private long occurrenceID, userID;
    private ArrayList<Long> mediaIDs;

    public MapObjectOccurrence()
    {

    }

    public double getUser_occurrence_lat() {
        return user_occurrence_lat;
    }

    public void setUser_occurrence_lat(double user_occurrence_lat) {
        this.user_occurrence_lat = user_occurrence_lat;
    }

    public double getUser_occurrence_lon() {
        return user_occurrence_lon;
    }

    public void setUser_occurrence_lon(double user_occurrence_lon) {
        this.user_occurrence_lon = user_occurrence_lon;
    }

    public int getUser_occurrence_level() {
        return user_occurrence_level;
    }

    public void setUser_occurrence_level(int user_occurrence_level) {
        this.user_occurrence_level = user_occurrence_level;
    }

    public boolean isUser_occurrence_visibility() {
        return user_occurrence_visibility;
    }

    public void setUser_occurrence_visibility(boolean user_occurrence_visibility) {
        this.user_occurrence_visibility = user_occurrence_visibility;
    }

    public boolean isUser_occurrence_notification_on_resolve() {
        return user_occurrence_notification_on_resolve;
    }

    public void setUser_occurrence_notification_on_resolve(boolean user_occurrence_notification_on_resolve) {
        this.user_occurrence_notification_on_resolve = user_occurrence_notification_on_resolve;
    }

    public String getUser_occurrence_type() {
        return user_occurrence_type;
    }

    public void setUser_occurrence_type(String user_occurrence_type) {
        this.user_occurrence_type = user_occurrence_type;
    }

    public String getUser_occurrence_description() {
        return user_occurrence_description;
    }

    public void setUser_occurrence_description(String user_occurrence_description) {
        this.user_occurrence_description = user_occurrence_description;
    }

    public String getUser_occurrence_title() {
        return user_occurrence_title;
    }

    public void setUser_occurrence_title(String user_occurrence_title) {
        this.user_occurrence_title = user_occurrence_title;
    }

    public String getUser_occurrence_date() {
        return user_occurrence_date;
    }

    public void setUser_occurrence_date(String user_occurrence_date) {
        this.user_occurrence_date = user_occurrence_date;
    }

    public long getOccurrenceID() {
        return occurrenceID;
    }

    public void setOccurrenceID(long occurrenceID) {
        this.occurrenceID = occurrenceID;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public ArrayList<Long> getMediaIDs() {
        return mediaIDs;
    }

    public void setMediaIDs(ArrayList<Long> mediaIDs) {
        this.mediaIDs = mediaIDs;
    }
}
