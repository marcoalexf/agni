package com.example.marisco.myapplication;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AgniAPI {

    //git works

    @POST("login")
    Call<LoginResponse> loginUser(@Body User user);

    @POST("register")
    Call<ResponseBody> registerUser(@Body UserRegister user);

    @POST("profile")
    Call<ProfileResponse> getProfile(@Body LoginResponse token);

}
