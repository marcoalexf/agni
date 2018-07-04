package com.example.marisco.myapplication;

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

    @POST("profile/username")
    Call<String> getUsername(@Body ProfileUsernameData request);

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

    @POST("occurrence/edit")
    Call<ResponseBody > editOccurrence(@Body OccurrenceEditData data);

    @POST("occurrence/comment")
    Call<ResponseBody> postComment(@Body OccurrenceCommentData data);

    @POST("occurrence/comment/list")
    Call<CursorList> getMoreComments(@Body ListOccurrenceCommentData data);

    @POST("occurrence/like")
    Call<ResponseBody> toggleLike(@Body OccurrenceDeleteData data);

    @POST("occurrence/like/list")
    Call<CursorList> getLikedOccurrences(@Body ListOccurrenceLikeData data);

    @POST("occurrence/like/count")
    Call<Integer> getLikes(@Body OccurrenceLikeCountData data);
}
