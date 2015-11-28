package com.example.android.popularmovies.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.app.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.app.data.MovieContract.ReviewsEntry;
import com.example.android.popularmovies.app.data.MovieContract.VideosEntry;

/**
 * Created by Nish on 15-11-2015.
 */
public class MovieDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 3;

    static final String DATABASE_NAME = "popularmovies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a table to hold locations.  A location consists of the string supplied in the
        // location setting, the city name, and the latitude and longitude
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_POSTER + " TEXT, " +
                MovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT, " +
                MovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT, " +
                MovieEntry.COLUMN_MOVIE_VOTE_AVG + " REAL, " +
                MovieEntry.COLUMN_MOVIE_POPULARITY + " REAL, " +
                MovieEntry.COLUMN_MOVIE_SORT_BY + " TEXT, " +
                MovieEntry.COLUMN_MOVIE_IS_FAVOURITE + " INTEGER " +
            " );";

        final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + ReviewsEntry.TABLE_NAME + " (" +
                ReviewsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ReviewsEntry.COLUMN_REVIEW_ID + " TEXT , " +
                ReviewsEntry.COLUMN_REVIEW_AUTHOR + " TEXT, " +
                ReviewsEntry.COLUMN_REVIEW_CONTENT + " TEXT NOT NULL, " +
                ReviewsEntry.COLUMN_REVIEW_URL + " TEXT, " +
                ReviewsEntry.COLUMN_MOVIE_ID + " INTEGER " +
            " )";

        final String SQL_CREATE_VIDEOS_TABLE = "CREATE TABLE " + VideosEntry.TABLE_NAME + " (" +
                VideosEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VideosEntry.COLUMN_VIDEO_ID + " TEXT , " +
                VideosEntry.COLUMN_VIDEO_KEY + " TEXT NOT NULL, " +
                VideosEntry.COLUMN_VIDEO_NAME + " TEXT NOT NULL, " +
                VideosEntry.COLUMN_VIDEO_SITE + " TEXT NOT NULL, " +
                VideosEntry.COLUMN_VIDEO_SIZE + " INTEGER, " +
                VideosEntry.COLUMN_VIDEO_TYPE + " TEXT, " +
                VideosEntry.COLUMN_MOVIE_ID + " INTEGER " +
            " )";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_VIDEOS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewsEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VideosEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
