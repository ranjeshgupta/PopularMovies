package com.example.android.popularmovies.app;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.app.data.MovieContract;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {

            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.DETAIL_URI, getIntent().getData());

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_sorting) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment implements LoaderCallbacks<Cursor> {
        private static final String MOVIE_SHARE_HASHTAG = "#PopularMovieApp";
        static final String DETAIL_URI = "URI";

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();
        private String mMovieId;
        private String mMovieName;
        private String mTrailerUrl;
        private Uri mUri;

        private static final int DETAIL_LOADER = 0;
        private static final int REVIEW_LOADER = 2;
        private static final int VIDEO_LOADER = 1;

        private static final String[] MOVIE_COLUMNS = {
                MovieContract.MovieEntry._ID,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
                MovieContract.MovieEntry.COLUMN_MOVIE_POSTER,
                MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
                MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
                MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVG,
                MovieContract.MovieEntry.COLUMN_MOVIE_POPULARITY,
                MovieContract.MovieEntry.COLUMN_MOVIE_IS_FAVOURITE
        };

        static final int COL_ID = 0;
        static final int COL_MOVIE_ID = 1;
        static final int COL_MOVIE_TITLE = 2;
        static final int COL_MOVIE_POSTER = 3;
        static final int COL_MOVIE_OVERVIEW = 4;
        static final int COL_MOVIE_RELEASE_DATE = 5;
        static final int COL_MOVIE_VOTE_AVG = 6;
        static final int COL_MOVIE_POPULARITY = 7;
        static final int COL_MOVIE_IS_FAVOURITE = 8;

        private static final String[] VIDEO_COLUMNS = {
                MovieContract.VideosEntry._ID,
                MovieContract.VideosEntry.COLUMN_VIDEO_ID,
                MovieContract.VideosEntry.COLUMN_VIDEO_KEY,
                MovieContract.VideosEntry.COLUMN_VIDEO_NAME,
                MovieContract.VideosEntry.COLUMN_VIDEO_SITE,
                MovieContract.VideosEntry.COLUMN_VIDEO_SIZE,
                MovieContract.VideosEntry.COLUMN_VIDEO_TYPE,
                MovieContract.VideosEntry.COLUMN_MOVIE_ID
        };
        static final int COL_VIDEO_ID = 1;
        static final int COL_VIDEO_KEY = 2;

        private static final String[] REVIEW_COLUMNS = {
                MovieContract.ReviewsEntry._ID,
                MovieContract.ReviewsEntry.COLUMN_REVIEW_ID,
                MovieContract.ReviewsEntry.COLUMN_REVIEW_AUTHOR,
                MovieContract.ReviewsEntry.COLUMN_REVIEW_CONTENT,
                MovieContract.ReviewsEntry.COLUMN_REVIEW_URL,
                MovieContract.ReviewsEntry.COLUMN_MOVIE_ID,
        };
        static final int COL_REVIEW_ID = 1;
        static final int COL_REVIEW_AUTHOR = 2;
        static final int COL_REVIEW_CONTENT = 3;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            Bundle arguments = getArguments();
            if (arguments != null) {
                mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
                mMovieId = MovieContract.MovieEntry.getMovieIdFromUri(mUri);
            }

            return inflater.inflate(R.layout.fragment_detail, container, false);
        }

        @Override
        public void onStart() {
            super.onStart();
            if (null != mMovieId) {
                getVideos();
                getReviews();
            }
        }

        private void getReviews() {
            FetchMovie fetchMovie = new FetchMovie(getActivity());
            fetchMovie.getReviews(mMovieId);
        }

        private void getVideos() {
            FetchMovie fetchMovie = new FetchMovie(getActivity());
            fetchMovie.getVideos(mMovieId);
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
            getLoaderManager().initLoader(VIDEO_LOADER, null, this);
            getLoaderManager().initLoader(REVIEW_LOADER, null, this);
            super.onActivityCreated(savedInstanceState);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.detail, menu);
            MenuItem menuItem = menu.findItem(R.id.action_share);
            ShareActionProvider mShareActionProvider;
            mShareActionProvider =
                    (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
            if (mShareActionProvider != null ) {
                if(mTrailerUrl != null) {
                    mShareActionProvider.setShareIntent(createShareMovieIntent());
                }
                else{
                    Log.d(LOG_TAG, "Movie Trailer Key is null?");
                }
            } else {
                Log.d(LOG_TAG, "Share Action Provider is null?");
            }
        }

        private Intent createShareMovieIntent() {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            shareIntent.setType("text/plain");

            String sharingText = "";
            if(mMovieName != null){
                sharingText = mMovieName + "\n";
            }
            sharingText = sharingText + mTrailerUrl + "\n" + MOVIE_SHARE_HASHTAG;
            shareIntent.putExtra(Intent.EXTRA_TEXT, sharingText);

            return shareIntent;
        }

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            Log.v(LOG_TAG, "In onCreateLoader");

            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            if(null != mMovieId) {
                switch (id) {
                    case DETAIL_LOADER: {
                        Uri movieDetailsUri = MovieContract.MovieEntry.buildMovieDetailsWithMovieId(mMovieId);
                        return new CursorLoader(
                                getActivity(),
                                movieDetailsUri,
                                MOVIE_COLUMNS,
                                null,
                                null,
                                null
                        );
                    }
                    case VIDEO_LOADER: {
                        Uri videoUri = MovieContract.VideosEntry.buildVideosWithMovieId(mMovieId);
                        return new CursorLoader(
                                getActivity(),
                                videoUri,
                                VIDEO_COLUMNS,
                                null,
                                null,
                                null
                        );
                    }
                    case REVIEW_LOADER: {
                        Uri reviewUri = MovieContract.ReviewsEntry.buildReviewsWithMovieId(mMovieId);
                        return new CursorLoader(
                                getActivity(),
                                reviewUri,
                                REVIEW_COLUMNS,
                                null,
                                null,
                                null
                        );
                    }
                    default:
                        throw new UnsupportedOperationException("Unknown loader id: " + id);
                }
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            Log.v(LOG_TAG, "In onLoadFinished " + loader.getId());

            if (loader.getId() == DETAIL_LOADER) {
                Log.v(LOG_TAG + " DETIAL LOADER", "Inside DETAIL Loader");
                if (!data.moveToFirst()) {
                    return;
                }

                mMovieName = data.getString(COL_MOVIE_TITLE);
                TextView movie_name = (TextView) getView().findViewById(R.id.movie_name);
                movie_name.setText(mMovieName);

                ImageView imageView = (ImageView) getView().findViewById(R.id.poster_image);
                Picasso.with(getContext())
                        .load(data.getString(COL_MOVIE_POSTER))
                        .placeholder(R.drawable.ic_placeholder) // optional
                        .error(R.drawable.ic_error_fallback)    // optional
                        .into(imageView);

                //favourite button
                View viewFavBg = (View) getView().findViewById(R.id.favourite_bg);
                viewFavBg.setVisibility(View.VISIBLE);

                ImageView imageViewFavourite = (ImageView) getView().findViewById(R.id.favourite);
                final int isFavourite = data.getInt(COL_MOVIE_IS_FAVOURITE);
                if(isFavourite == 1){
                    Picasso.with(getContext())
                            .load(R.drawable.ic_favorite)
                            .into(imageViewFavourite);
                }
                else{
                    Picasso.with(getContext())
                            .load(R.drawable.ic_favorite_outline)
                            .into(imageViewFavourite);
                }
                imageViewFavourite.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        //Toast.makeText(getContext(), "Is Favourite Movie = " + Integer.toString(isFavourite), Toast.LENGTH_SHORT).show();

                        ContentValues movieValues = new ContentValues();
                        if (isFavourite == 1) {
                            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_IS_FAVOURITE, 0);
                        } else {
                            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_IS_FAVOURITE, 1);
                        }

                        final String sMovieWithMovieIdSelection =
                                MovieContract.MovieEntry.TABLE_NAME +
                                        "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";
                        int rowsUpdated;
                        rowsUpdated = getContext().getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI,
                                movieValues, sMovieWithMovieIdSelection, new String[]{mMovieId});
                        if (rowsUpdated == 0) {
                            Toast.makeText(getContext(),
                                    "Unable to " + (isFavourite == 1 ? "remove from" : "add to") + " favourite list.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(),
                                    "Success fully " + (isFavourite == 1 ? "removed from" : "added to") + " favourite list.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                TextView release_date = (TextView) getView().findViewById(R.id.release_date);
                release_date.setText(data.getString(COL_MOVIE_RELEASE_DATE));

                TextView rating = (TextView) getView().findViewById(R.id.rating);
                rating.setText(Float.toString(data.getFloat(COL_MOVIE_VOTE_AVG)) + "/10");

                TextView overviewHead = (TextView) getView().findViewById(R.id.synopsis);
                overviewHead.setText(R.string.label_synopsis_head);

                TextView overview = (TextView) getView().findViewById(R.id.overview);
                overview.setText(data.getString(COL_MOVIE_OVERVIEW));

                RatingBar rating_bar = (RatingBar) getView().findViewById(R.id.rating_bar);
                rating_bar.setRating(data.getFloat(COL_MOVIE_VOTE_AVG) / 2);
                rating_bar.setVisibility(View.VISIBLE);

                getActivity().setTitle(mMovieName);
            }

            else if (loader.getId() == VIDEO_LOADER) {
                Log.v(LOG_TAG + " VIDEO LOADER", "Inside VIDEO Loader");
                if (!data.moveToFirst()) {
                    Log.v(LOG_TAG + " VIDEO LOADER", "Inside is data found check ");
                    return;
                }

                //trailer view
                TextView trailerHead = (TextView) getView().findViewById(R.id.trailer_head);
                trailerHead.setVisibility(View.VISIBLE);

                int i = 0;
                while (data.isAfterLast() == false && i<3) {
                    Log.v(LOG_TAG + " VIDEO LOADER : ", data.getString(COL_VIDEO_KEY));

                    i++;
                    ImageView imageView;
                    imageView = (ImageView) getView().findViewById(R.id.video_image_01);

                    ImageView imageViewYtPlay;
                    imageViewYtPlay = (ImageView) getView().findViewById(R.id.yt_play_button_01);

                    if(i == 1){
                        mTrailerUrl = "https://www.youtube.com/watch?v=" + data.getString(COL_VIDEO_KEY);
                    }

                    if (i == 2) {
                        imageView = (ImageView) getView().findViewById(R.id.video_image_02);
                        imageViewYtPlay = (ImageView) getView().findViewById(R.id.yt_play_button_02);
                    }
                    else if (i == 3) {
                        imageView = (ImageView) getView().findViewById(R.id.video_image_03);
                        imageViewYtPlay = (ImageView) getView().findViewById(R.id.yt_play_button_03);
                    }

                    imageView.setVisibility(View.VISIBLE);
                    imageViewYtPlay.setVisibility(View.VISIBLE);

                    final String videoId = data.getString(COL_VIDEO_KEY);
                    Picasso.with(getContext())
                            .load("http://img.youtube.com/vi/" + videoId + "/1.jpg")
                            .placeholder(R.drawable.ic_placeholder) // optional
                            .error(R.drawable.ic_error_fallback)    // optional
                            .resize(120, 90)
                            .centerCrop()
                            .into(imageView);

                    imageView.setOnClickListener(new ImageView.OnClickListener() {
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + videoId));
                            intent.putExtra("VIDEO_ID", videoId);
                            startActivity(intent);
                        }
                    });

                    data.moveToNext();
                }

            }
            else if (loader.getId() == REVIEW_LOADER) {
                Log.v(LOG_TAG + " REVIEW LOADER", "Inside REVIEW Loader");
                if (!data.moveToFirst()) {
                    getView().findViewById(R.id.reviews_head).setVisibility(View.GONE);
                    getView().findViewById(R.id.reviews).setVisibility(View.GONE);
                    return;
                }

                TextView reviewsHead = (TextView) getView().findViewById(R.id.reviews_head);
                reviewsHead.setText("Reviews:");

                String reviewStr = "";
                while (data.isAfterLast() == false) {
                    reviewStr = reviewStr + "<p>"
                            + "<b>" + data.getString(COL_REVIEW_AUTHOR) + ": </b><br>"
                            + data.getString(COL_REVIEW_CONTENT).replace("\r\n\r\n", "<br>")
                            + "</p>";
                    data.moveToNext();
                }
                TextView reviews = (TextView) getView().findViewById(R.id.reviews);
                reviews.setText(Html.fromHtml(reviewStr));
            }
            else{
                throw new UnsupportedOperationException("Unknown loader id on finish : " + loader.getId());
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    }
}