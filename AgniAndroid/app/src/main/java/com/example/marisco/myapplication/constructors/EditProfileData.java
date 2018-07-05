package com.example.marisco.myapplication.constructors;

import com.example.marisco.myapplication.LoginResponse;

public class EditProfileData {

    public String username;
    public LoginResponse token;
    public String role;
    public String district;
    public String county;
    public String locality;
    public String email;
    public boolean uploadPhoto;

    public EditProfileData() {

    }

    public EditProfileData(LoginResponse token, String username, String email, String district, String county, String locality, boolean uploadPhoto) {
        this.username = username;
        this.token = token;
        this.email = email;
        this.district = district;
        this.county = county;
        this.locality = locality;
        this.uploadPhoto = uploadPhoto;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LoginResponse getToken() {
        return token;
    }

    public void setToken(LoginResponse token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isUploadPhoto() {
        return uploadPhoto;
    }

    public void setUploadPhoto(boolean uploadPhoto) {
        this.uploadPhoto = uploadPhoto;
    }
}
