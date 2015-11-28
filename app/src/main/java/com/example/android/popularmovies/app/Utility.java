package com.example.android.popularmovies.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.android.popularmovies.app.data.MovieContract;

/**
 * Created by Nish on 17-11-2015.
 */
public class Utility {
    public static String getPreferredSorting(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_default));
    }

    public static String getSortBy(String sort_by){
        if (sort_by.equals("popularity.desc") || sort_by.equals("popularity")) {
            return  "popularity";
        }
        else if (sort_by.equals("vote_average.desc") || sort_by.equals("vote_average")) {
            return  "vote_average";
        }
        else if (sort_by.equals("favourite")) {
            return  "favourite";
        }
        else {
            return  "popularity";
        }
    }

    public static String getSortOrder(String sort_by){
        sort_by = getSortBy(sort_by);

        if (sort_by.equals("popularity.desc") || sort_by.equals("popularity")) {
            return MovieContract.MovieEntry.COLUMN_MOVIE_POPULARITY + " DESC ";
        }
        else if (sort_by.equals("vote_average.desc") || sort_by.equals("vote_average")) {
            return MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVG + " DESC, "
                    + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " ASC ";
        }
        else if (sort_by.equals("favourite")) {
            return MovieContract.MovieEntry.COLUMN_MOVIE_IS_FAVOURITE + " DESC, "
                    + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " ASC ";
        }
        else {
            return MovieContract.MovieEntry.COLUMN_MOVIE_ID + " ASC ";
        }
    }
}
