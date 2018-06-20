package com.example.marisco.myapplication;

import com.google.gson.annotations.SerializedName;

public class ProfileResponse {

    @SerializedName("user_username") private String username;

    @SerializedName("user_name") private String name;

    @SerializedName("user_role") private String role;

    @SerializedName("user_email") private String email;

    @SerializedName("user_district") private String district;

    @SerializedName("user_county") private String county;

    @SerializedName("user_locality") private String locality;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
