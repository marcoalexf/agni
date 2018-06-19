package com.example.marisco.myapplication;

import com.google.android.gms.maps.model.LatLng;

public class SavedLocation {

    private String title;
    private LatLng location;

    public SavedLocation(String title, LatLng location){
        this.title = title;
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public LatLng getLocation() {
        return location;
    }
}
