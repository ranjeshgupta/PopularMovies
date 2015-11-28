package com.example.android.popularmovies.app;

import retrofit.Callback;
import retrofit.http.GET;

/**
 * Created by Nish on 04-11-2015.
 */
public interface MoviesApiService {
    @GET("/movie")
    void getPopularMovies(Callback<Movie.MovieResult> cb);
}
