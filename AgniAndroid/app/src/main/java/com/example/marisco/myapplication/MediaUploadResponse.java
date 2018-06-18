package com.example.marisco.myapplication;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MediaUploadResponse {

    @SerializedName("") List<Long> response;

    public MediaUploadResponse(){

    }

    public List<Long> getList(){
        return this.response;
    }
}
