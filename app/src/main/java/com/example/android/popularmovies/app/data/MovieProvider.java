package com.example.android.popularmovies.app.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.app.Utility;

/**
 * Created by Nish on 15-11-2015.
 */
public class MovieProvider extends ContentProvider {
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int MOVIE = 100;
    static final int MOVIE_DETAILS_BY_MOVIE_ID = 101;
    static final int REVIEWS = 102;
    static final int VIDEOS = 103;
    static final int MOVIE_WITH_SORT_BY = 104;

    private static final SQLiteQueryBuilder sMovieQueryBuilder;
    private static final SQLiteQueryBuilder sReviewQueryBuilder;
    private static final SQLiteQueryBuilder sVideoQueryBuilder;

    static{
        sMovieQueryBuilder = new SQLiteQueryBuilder();
        sMovieQueryBuilder.setTables(MovieContract.MovieEntry.TABLE_NAME);

        sReviewQueryBuilder = new SQLiteQueryBuilder();
        sReviewQueryBuilder.setTables(MovieContract.ReviewsEntry.TABLE_NAME);

        sVideoQueryBuilder = new SQLiteQueryBuilder();
        sVideoQueryBuilder.setTables(MovieContract.VideosEntry.TABLE_NAME);
    }

    private static final String sMovieWithSortBy =
            MovieContract.MovieEntry.TABLE_NAME+
                    "." + MovieContract.MovieEntry.COLUMN_MOVIE_SORT_BY + " = ? ";

    private static final String sMovieWithSortByAndFavourite =
            MovieContract.MovieEntry.TABLE_NAME+
                    "." + MovieContract.MovieEntry.COLUMN_MOVIE_SORT_BY + " = ? " +
                " AND " + MovieContract.MovieEntry.TABLE_NAME+
                    "." + MovieContract.MovieEntry.COLUMN_MOVIE_IS_FAVOURITE + " = ? ";

    private static final String sMovieFavourite =
            MovieContract.MovieEntry.TABLE_NAME+
                    "." + MovieContract.MovieEntry.COLUMN_MOVIE_IS_FAVOURITE + " = ? ";

    private static final String sMovieDetailsWithMovieIdSelection =
            MovieContract.MovieEntry.TABLE_NAME+
                    "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";

    private static final String sReviewsWithMovieIdSelection =
            MovieContract.ReviewsEntry.TABLE_NAME+
                    "." + MovieContract.ReviewsEntry.COLUMN_MOVIE_ID + " = ? ";

    private static final String sVideosWithMovieIdSelection =
            MovieContract.VideosEntry.TABLE_NAME+
                    "." + MovieContract.VideosEntry.COLUMN_MOVIE_ID + " = ? "+
                " AND " + MovieContract.VideosEntry.TABLE_NAME+
                    "." + MovieContract.VideosEntry.COLUMN_VIDEO_SITE + " = ? ";

    private Cursor getMovieWithSortBy(
            Uri uri, String[] projection, String sortOrder) {
        String sortBy = MovieContract.MovieEntry.getSortByFromUri(uri);

        if(sortBy.equals("favourite")) {
            return sMovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                    projection,
                    sMovieFavourite,
                    new String[]{"1"},
                    null,
                    null,
                    sortOrder
            );
        }
        else {
            return sMovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                    projection,
                    sMovieWithSortBy,
                    new String[]{sortBy},
                    null,
                    null,
                    sortOrder
            );
        }
    }

    private Cursor getMovieDetailsByMovieId(
            Uri uri, String[] projection, String sortOrder) {
        String movieId = MovieContract.MovieEntry.getMovieIdFromUri(uri);

        return sMovieQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sMovieDetailsWithMovieIdSelection,
                new String[]{movieId},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getReviewsByMovieId(
            Uri uri, String[] projection, String sortOrder) {
        String movieId = MovieContract.ReviewsEntry.getMovieIdFromUri(uri);

        return sReviewQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sReviewsWithMovieIdSelection,
                new String[]{movieId},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getVideosByMovieId(
            Uri uri, String[] projection, String sortOrder) {
        String movieId = MovieContract.VideosEntry.getMovieIdFromUri(uri);

        return sVideoQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sVideosWithMovieIdSelection,
                new String[]{movieId, "YouTube"},
                null,
                null,
                sortOrder
        );
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_DETAILS_BY_MOVIE_ID);
        matcher.addURI(authority, MovieContract.PATH_REVIEWS + "/#", REVIEWS);
        matcher.addURI(authority, MovieContract.PATH_VIDEOS + "/#", VIDEOS);
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/*", MOVIE_WITH_SORT_BY);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_SORT_BY:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_DETAILS_BY_MOVIE_ID:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case REVIEWS:
                return MovieContract.ReviewsEntry.CONTENT_TYPE;
            case VIDEOS:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIE: {
                String sort_by = Utility.getPreferredSorting(getContext());
                sortOrder = Utility.getSortOrder(sort_by);

                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case MOVIE_WITH_SORT_BY: {
                String sort_by = Utility.getPreferredSorting(getContext());
                sortOrder = Utility.getSortOrder(sort_by);
                retCursor = getMovieWithSortBy(uri, projection, sortOrder);
                break;
            }
            case MOVIE_DETAILS_BY_MOVIE_ID: {
                retCursor = getMovieDetailsByMovieId(uri, projection, sortOrder);
                break;
            }
            case REVIEWS: {
                retCursor = getReviewsByMovieId(uri, projection, sortOrder);
                break;
            }
            case VIDEOS: {
                retCursor = getVideosByMovieId(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case MOVIE: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieEntry.buildMovieUri();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case MOVIE_WITH_SORT_BY: {
                String sortBy = MovieContract.MovieEntry.getSortByFromUri(uri);
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MovieEntry.buildMovieWithSortBy(sortBy);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEWS: {
                long _id = db.insert(MovieContract.ReviewsEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.ReviewsEntry.buildReviewsUri();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case VIDEOS: {
                long _id = db.insert(MovieContract.VideosEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.VideosEntry.buildVideosUri();
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, sMovieWithSortByAndFavourite, selectionArgs);
                break;
            case MOVIE_WITH_SORT_BY:
                rowsDeleted = db.delete(
                        MovieContract.MovieEntry.TABLE_NAME, sMovieWithSortByAndFavourite, selectionArgs);
                break;
            case REVIEWS:
                rowsDeleted = db.delete(
                        MovieContract.ReviewsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case VIDEOS:
                rowsDeleted = db.delete(
                        MovieContract.VideosEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case MOVIE_WITH_SORT_BY:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case REVIEWS:
                rowsUpdated = db.update(MovieContract.ReviewsEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case VIDEOS:
                rowsUpdated = db.update(MovieContract.VideosEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount;
        switch (match) {
            case MOVIE: {
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            case MOVIE_WITH_SORT_BY: {
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            case REVIEWS: {
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.ReviewsEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            case VIDEOS: {
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.VideosEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            default:
                return super.bulkInsert(uri, values);
        }
        if (returnCount != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return returnCount;
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}