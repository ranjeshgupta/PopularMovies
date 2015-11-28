package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.app.data.MovieContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Nish on 16-11-2015.
 */
public class FetchMovie {
    private final String LOG_TAG = FetchMovie.class.getSimpleName();
    private final String API_KEY = "806b9c0290fa60294e1bc9a5d7e6e932";

    private final Context mContext;

    public FetchMovie(Context context) {
        mContext = context;
    }

    private boolean DEBUG = true;

    public void getPopularMovies() {
        RestAdapter restAdapter = new RestAdapter.Builder()
                //http://api.themoviedb.org/3/discover/movie
                .setEndpoint("http://api.themoviedb.org/3/discover")
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addEncodedQueryParam("api_key", API_KEY);

                        String sort_by = Utility.getPreferredSorting(mContext);
                        request.addEncodedQueryParam("sort_by", sort_by);
                    }
                })
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        MoviesApiService service = restAdapter.create(MoviesApiService.class);
        service.getPopularMovies(new Callback<Movie.MovieResult>() {
            @Override
            public void success(Movie.MovieResult movieResult, Response response) {
                //mAdapter.setMovieList(movieResult.getResults());
                List<Movie> movieList;
                movieList = new ArrayList<>();
                movieList.addAll(movieResult.getResults());

                int deleted;
                String sort_by = Utility.getSortBy(Utility.getPreferredSorting(mContext));
                Uri movieUri = MovieContract.MovieEntry.buildMovieWithSortBy(Utility.getSortBy(sort_by));
                deleted = mContext.getContentResolver()
                        .delete(movieUri, null
                                , new String[] {sort_by, "0"});
                Log.d(LOG_TAG, "FetchMovie Initialized. " + deleted + " Deleted");

                Vector<ContentValues> cVVector = new Vector<ContentValues>(movieList.size());
                for(int i = 0; i < movieList.size(); i++) {
                    Movie movie = movieList.get(i);
                    ContentValues movieValues = new ContentValues();
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, movie.getPoster());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, movie.getOverview());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, movie.getReleaseDate());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVG, movie.getVoteAvg());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POPULARITY, movie.getPopularity());
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_SORT_BY, sort_by);
                    movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_IS_FAVOURITE, 0);

                    cVVector.add(movieValues);
                }

                int inserted = 0;
                // add to database
                if ( cVVector.size() > 0 ) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    inserted = mContext.getContentResolver().bulkInsert(movieUri, cvArray);
                }

                Log.d(LOG_TAG, "FetchMovie Complete. " + inserted + " Inserted");
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }


    public void getReviews(final String movieId) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                //http://api.themoviedb.org/3/movie/{movieid}/reviews
                .setEndpoint("http://api.themoviedb.org/3/movie/" + movieId)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addEncodedQueryParam("api_key", API_KEY);
                    }
                })
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        ReviewApiService service = restAdapter.create(ReviewApiService.class);
        service.getReviews(new Callback<Review.ReviewResult>() {
            @Override
            public void success(Review.ReviewResult reviewResult, Response response) {
                List<Review> reviewList;
                reviewList = new ArrayList<>();
                reviewList.addAll(reviewResult.getResults());

                int deleted;
                Uri reviewUri = MovieContract.ReviewsEntry.buildReviewsWithMovieId(movieId);
                final String sReviewsWithMovieIdSelection = MovieContract.ReviewsEntry.TABLE_NAME +
                        "." + MovieContract.ReviewsEntry.COLUMN_MOVIE_ID + " = ? ";
                deleted = mContext.getContentResolver().delete(reviewUri, sReviewsWithMovieIdSelection, new String[]{movieId});
                Log.d(LOG_TAG, "FetchReview Initialized. " + deleted + " Deleted");

                Vector<ContentValues> cVVector = new Vector<ContentValues>(reviewList.size());
                for (int i = 0; i < reviewList.size(); i++) {
                    Review review = reviewList.get(i);
                    ContentValues reviewValues = new ContentValues();
                    reviewValues.put(MovieContract.ReviewsEntry.COLUMN_REVIEW_ID, review.getId());
                    reviewValues.put(MovieContract.ReviewsEntry.COLUMN_REVIEW_AUTHOR, review.getAuthor());
                    reviewValues.put(MovieContract.ReviewsEntry.COLUMN_REVIEW_CONTENT, review.getContent());
                    reviewValues.put(MovieContract.ReviewsEntry.COLUMN_REVIEW_URL, review.getUrl());
                    reviewValues.put(MovieContract.ReviewsEntry.COLUMN_MOVIE_ID, movieId);

                    cVVector.add(reviewValues);
                }

                int inserted = 0;
                // add to database
                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    inserted = mContext.getContentResolver().bulkInsert(reviewUri, cvArray);
                }

                Log.d(LOG_TAG, "FetchReview Complete. " + inserted + " Inserted");
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    public void getVideos(final String movieId) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                //http://api.themoviedb.org/3/movie/{movieid}/videos
                .setEndpoint("http://api.themoviedb.org/3/movie/" + movieId)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addEncodedQueryParam("api_key", API_KEY);
                    }
                })
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();
        VideoApiService service = restAdapter.create(VideoApiService.class);
        service.getVideos(new Callback<Video.VideoResult>() {
            @Override
            public void success(Video.VideoResult videoResult, Response response) {
                List<Video> videoList;
                videoList = new ArrayList<>();
                videoList.addAll(videoResult.getResults());

                int deleted;
                Uri videoUri = MovieContract.VideosEntry.buildVideosWithMovieId(movieId);
                final String sVideosWithMovieIdSelection = MovieContract.VideosEntry.TABLE_NAME +
                        "." + MovieContract.VideosEntry.COLUMN_MOVIE_ID + " = ? ";
                deleted = mContext.getContentResolver().delete(videoUri, sVideosWithMovieIdSelection, new String[]{movieId});
                Log.d(LOG_TAG, "FetchVideo Initialized. " + deleted + " Deleted");

                Vector<ContentValues> cVVector = new Vector<ContentValues>(videoList.size());
                for (int i = 0; i < videoList.size(); i++) {
                    Video video = videoList.get(i);
                    ContentValues videoValues = new ContentValues();
                    videoValues.put(MovieContract.VideosEntry.COLUMN_VIDEO_ID, video.getId());
                    videoValues.put(MovieContract.VideosEntry.COLUMN_VIDEO_KEY, video.getKey());
                    videoValues.put(MovieContract.VideosEntry.COLUMN_VIDEO_NAME, video.getName());
                    videoValues.put(MovieContract.VideosEntry.COLUMN_VIDEO_SITE, video.getSite());
                    videoValues.put(MovieContract.VideosEntry.COLUMN_VIDEO_SIZE, video.getSize());
                    videoValues.put(MovieContract.VideosEntry.COLUMN_VIDEO_TYPE, video.getType());
                    videoValues.put(MovieContract.VideosEntry.COLUMN_MOVIE_ID, movieId);
                    cVVector.add(videoValues);
                }

                int inserted = 0;
                // add to database
                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    inserted = mContext.getContentResolver().bulkInsert(videoUri, cvArray);
                }

                Log.d(LOG_TAG, "FetchVideo Complete. " + inserted + " Inserted");
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }
}
