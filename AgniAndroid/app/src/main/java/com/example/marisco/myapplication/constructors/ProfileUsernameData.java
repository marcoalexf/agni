package com.example.marisco.myapplication.constructors;

public class ProfileUsernameData {

    public double userID;

    public ProfileUsernameData() {

    }

    public ProfileUsernameData(double userID) {
        this.userID = userID;
    }


    public double getUserID() {
        return userID;
    }

    public void setUserID(double userID) {
        this.userID = userID;
    }

}