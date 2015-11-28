package com.example.android.popularmovies.app.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Nish on 15-11-2015.
 */
public class MovieContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies.app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.popularmovies.app/popularmovie/ is a valid path for
    // looking at weather data. content://com.example.android.popularmovies.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_MOVIE = "popularmovies";
    public static final String PATH_REVIEWS = "reviews";
    public static final String PATH_VIDEOS = "videos";

    /* Inner class that defines the table contents of the location table */
    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        // Table name
        public static final String TABLE_NAME = "popularmovies";

        public static final String COLUMN_MOVIE_ID = "id";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_POSTER = "poster";
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "release_date";
        public static final String COLUMN_MOVIE_VOTE_AVG = "vote_avg";
        public static final String COLUMN_MOVIE_POPULARITY = "popularity";
        public static final String COLUMN_MOVIE_SORT_BY = "sort_by";
        public static final String COLUMN_MOVIE_IS_FAVOURITE = "is_favourite";

        public static Uri buildMovieUri() {
            return CONTENT_URI;
        }

        public static Uri buildMovieDetailsWithMovieId(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

        public static Uri buildMovieWithSortBy(String sortBy) {
            return CONTENT_URI.buildUpon().appendPath(sortBy).build();
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getSortByFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class ReviewsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEWS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;

        // Table name
        public static final String TABLE_NAME = "reviews";

        public static final String COLUMN_REVIEW_ID = "id";
        public static final String COLUMN_REVIEW_AUTHOR = "author";
        public static final String COLUMN_REVIEW_CONTENT = "content";
        public static final String COLUMN_REVIEW_URL = "url";
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static Uri buildReviewsUri() {
            return CONTENT_URI;
        }

        public static Uri buildReviewsWithMovieId(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class VideosEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VIDEOS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEWS;

        // Table name
        public static final String TABLE_NAME = "videos";

        public static final String COLUMN_VIDEO_ID = "id";
        public static final String COLUMN_VIDEO_KEY = "key";
        public static final String COLUMN_VIDEO_NAME = "name";
        public static final String COLUMN_VIDEO_SITE = "site";
        public static final String COLUMN_VIDEO_SIZE = "size";
        public static final String COLUMN_VIDEO_TYPE = "type";
        public static final String COLUMN_MOVIE_ID = "movie_id";

        public static Uri buildVideosUri() {
            return CONTENT_URI;
        }

        public static Uri buildVideosWithMovieId(String movieId) {
            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }

        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
