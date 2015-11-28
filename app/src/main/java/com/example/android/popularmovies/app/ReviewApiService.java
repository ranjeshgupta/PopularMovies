package com.example.android.popularmovies.app;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by Nish on 21-11-2015.
 */
public interface ReviewApiService {
    @GET("/reviews")
    void getReviews(Callback<Review.ReviewResult> cb);
}
