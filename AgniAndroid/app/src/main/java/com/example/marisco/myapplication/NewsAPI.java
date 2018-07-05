package com.example.marisco.myapplication;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface NewsAPI {

    @GET("top-headlines")
    Call<NewsDataCard> topHeadlines(@QueryMap Map<String, String> options);

}
