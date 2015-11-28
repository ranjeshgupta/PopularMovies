package com.example.android.popularmovies.app;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Nish on 04-11-2015.
 */
public class Movie implements Parcelable {
    @SerializedName("id")
    private String id;
    @SerializedName("original_title")
    private String title;
    @SerializedName("poster_path")
    private String poster;
    @SerializedName("overview")
    String overview;
    @SerializedName("release_date")
    String release_date;
    @SerializedName("vote_average")
    float vote_avg;
    @SerializedName("popularity")
    float popularity;

    public Movie() {}

    protected Movie(Parcel in) {
        id = in.readString();
        title = in.readString();
        poster = in.readString();
        overview = in.readString();
        release_date = in.readString();
        vote_avg = in.readFloat();
        popularity = in.readFloat();
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return "http://image.tmdb.org/t/p/w185/" + poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return release_date;
    }

    public void setReleaseDate(String release_date) {
        this.release_date = release_date;
    }

    public Float getVoteAvg() {
        return vote_avg;
    }

    public void setVoteAvg(float vote_avg) {
        this.vote_avg = vote_avg;
    }

    public Float getPopularity() {
        return popularity;
    }

    public void setPopularity(float popularity) {
        this.popularity = popularity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(poster);
        parcel.writeString(overview);
        parcel.writeString(release_date);
        parcel.writeFloat(vote_avg);
        parcel.writeFloat(popularity);
    }

    public static class MovieResult {
        private List<Movie> results;

        public List<Movie> getResults() {
            return results;
        }
    }
}
