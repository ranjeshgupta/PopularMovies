package com.example.android.popularmovies.app;

/**
 * Created by Nish on 18-10-2015.
 */
public class MyMovieObject {
    String id;
    String title;
    String poster;
    String overview;
    String release_date;
    float vote_avg;

    MyMovieObject(String id, String title, String poster) {
        this.id = id;
        this.title = title;
        this.poster = poster;
    }

    MyMovieObject(String id, String title, String poster, String overview, String release_date, float vote_avg) {
        this.id = id;
        this.title = title;
        this.poster = poster;
        this.overview = overview;
        this.release_date = release_date;
        this.vote_avg = vote_avg;
    }
};
