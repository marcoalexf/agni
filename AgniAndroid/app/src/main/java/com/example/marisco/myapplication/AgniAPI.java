package com.example.marisco.myapplication;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AgniAPI {

    //git works

    @POST("login")
    Call<LoginResponse> loginUser(@Body User user);

    @POST("register")
    Call<LoginResponse> registerUser(@Body UserRegister user);

}
