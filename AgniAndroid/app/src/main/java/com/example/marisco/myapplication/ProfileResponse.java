package com.example.marisco.myapplication;

import com.google.gson.annotations.SerializedName;

public class ProfileResponse {

    @SerializedName("name") private String name;

    @SerializedName("role") private String role;

    @SerializedName("email") private String email;

    @SerializedName("username") private String username;

    @SerializedName("district") private String district;

    @SerializedName("county") private String county;

    @SerializedName("locality") private String locality;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }
}
