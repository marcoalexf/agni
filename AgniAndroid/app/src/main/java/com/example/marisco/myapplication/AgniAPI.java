package com.example.marisco.myapplication;

import java.io.File;
import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface AgniAPI {

    @POST("login")
    Call<LoginResponse> loginUser(@Body User user);

    @POST("logout")
    Call<ResponseBody> logoutUser(@Body LoginResponse token);

    @POST("register")
    Call<ResponseBody> registerUser(@Body UserRegister user);

    @POST("profile")
    Call<ProfileResponse> getProfile(@Body ProfileRequest request);

    @POST("profile/edit")
    Call<ResponseBody> changeProfile(@Body EditProfileData request);

    @POST("occurrence/register")
    Call<ResponseBody> registerOccurrence(@Body OccurrenceData data);

    @POST("occurrence/register")
    Call<MediaUploadResponse> registerOccurrencePhoto(@Body OccurrenceData data);

    @POST("upload/{id}")
    Call<ResponseBody> uploadPhoto(@Path("id") Long l, @Header("Content-Type") String contentType, @Body RequestBody file);

    @GET("occurrence/list")
    Call<CursorList > getOccurrences();

    @POST("occurrence/list")
    Call<CursorList > getMoreOccurrences(@Body ListOccurrenceData data);
}
