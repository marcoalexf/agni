package com.example.marisco.myapplication;

import com.google.gson.annotations.SerializedName;

public class UserRegister{

    @SerializedName("name") private String name;

    @SerializedName("role") private String role;

    @SerializedName("email") private String email;

    @SerializedName("username") private String username;

    @SerializedName("district") private String district;

    @SerializedName("county") private String county;

    @SerializedName("locality") private String locality;

    @SerializedName("password") private String password;

    public UserRegister(String name, String username, String password, String email, String role,
                        String locality, String county, String district){
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.county = county;
        this.district = district;
        this.locality = locality;
        this.name = name;
    }

    public String getUser_type() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public void setUser_type(String role) {
        this.role = role;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
