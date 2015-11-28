package com.example.android.popularmovies.app;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by Nish on 21-11-2015.
 */
public interface VideoApiService {
    @GET("/videos")
    void getVideos(Callback<Video.VideoResult> cb);
}
