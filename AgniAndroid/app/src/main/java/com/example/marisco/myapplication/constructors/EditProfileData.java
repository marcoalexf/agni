package com.example.marisco.myapplication.constructors;

import com.example.marisco.myapplication.LoginResponse;

public class EditProfileData {

    public String name;
        public LoginResponse token;
        public Long userID;
        public String role;
        public String district;
        public String county;
        public String locality;
        public String email;
        public String newUsername;
        public String password;
        public String newPassword;
        public boolean uploadPhoto;

        public EditProfileData() {

        }

        public EditProfileData(LoginResponse token, Long userID, String newUsername, String password,
                               String newPassword, String email, String name, String district, String county, String locality,
                               boolean uploadPhoto) {
            this.name = name;
            this.token = token;
            this.userID = userID;
            this.newUsername = newUsername;
            this.password = password;
            this.newPassword = newPassword;
            this.email = email;
            this.district = district;
            this.county = county;
            this.locality = locality;
            this.uploadPhoto = uploadPhoto;
        }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public boolean isUploadPhoto() {
        return uploadPhoto;
    }

    public void setUploadPhoto(boolean uploadPhoto) {
        this.uploadPhoto = uploadPhoto;
    }
}
