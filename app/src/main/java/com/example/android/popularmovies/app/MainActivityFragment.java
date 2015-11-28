package com.example.android.popularmovies.app;

import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;

import com.example.android.popularmovies.app.data.MovieContract;

/**
 * A placeholder fragment containing a simple view.
 */

public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MOVIE_LOADER = 0;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.COLUMN_MOVIE_POSTER,
            MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_MOVIE_VOTE_AVG,
            MovieContract.MovieEntry.COLUMN_MOVIE_POPULARITY
    };

    static final int COL_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_MOVIE_TITLE = 2;
    static final int COL_MOVIE_POSTER = 3;
    static final int COL_MOVIE_OVERVIEW = 4;
    static final int COL_MOVIE_RELEASE_DATE = 5;
    static final int COL_MOVIE_VOTE_AVG = 6;
    static final int COL_MOVIE_POPULARITY = 7;

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri movieUri);
    }


    public MainActivityFragment() {
    }

    private MovieAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    int fragmentWidth;
    int numColumns;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // The CursorAdapter will take data from our cursor and populate the ListView.

        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar1);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        final View myView = getView();
        myView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                fragmentWidth = myView.findViewById(R.id.fragment_main).getWidth();
                if(fragmentWidth > 0) {
                    //Log.e("Fragment width", Integer.toString(fragmentWidth));
                    numColumns = determineColumns(fragmentWidth);
                    //Log.e("num of columns", Integer.toString(numColumns));

                    mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), numColumns));
                    myView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
        numColumns = 2;
        mRecyclerView = (RecyclerView) myView.findViewById(R.id.recycler_view);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(2)); //1px space from all four sides
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), numColumns));
        mAdapter = new MovieAdapter(getActivity(), null);
        mRecyclerView.setAdapter(mAdapter);

        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
        mProgressBar.setVisibility(View.GONE);
    }

    void onSortingChanged( ) {
        if(Utility.getPreferredSorting(getActivity()).equals("favourite")) {
            Log.d("in location changed fav", Utility.getPreferredSorting(getActivity()));
        }
        else{
            getPopularMovies();
        }
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }

    private void getPopularMovies() {
        FetchMovie fetchMovie = new FetchMovie(getActivity());
        fetchMovie.getPopularMovies();
    }

    /*
    @Override
    public void onStart() {
        super.onStart();
        getPopularMovies();
    }
    */

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri movieUri = MovieContract.MovieEntry.buildMovieWithSortBy(Utility.getSortBy(Utility.getPreferredSorting(getContext())));
        Log.d("Cursor Loader", Utility.getSortBy(Utility.getPreferredSorting(getContext())));
        return new CursorLoader(getActivity(),
                movieUri,
                MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }

    private int determineColumns(int availableSpace) {
        int mRequestedNumColumns = -1;
        float scalefactor = getResources().getDisplayMetrics().density;
        int requestedColumnWidth = (int) (185 * scalefactor);

        int mNumColumns = 1;
        int requestedHorizontalSpacing = 1;

        if (mRequestedNumColumns == -1) {
            if (requestedColumnWidth > 0) {
                // Client told us to pick the number of columns
                mNumColumns = (int) (Math.round( ((double) (availableSpace) + (double) (requestedHorizontalSpacing)) /
                        ((double) (requestedColumnWidth) + (double) (requestedHorizontalSpacing)) ));
            } else {
                // Just make up a number if we don't have enough info
                mNumColumns = 2;
            }
        } else {
            // We picked the columns
            mNumColumns = mRequestedNumColumns;
        }
        if (mNumColumns <= 0) {
            mNumColumns = 1;
        }
        //Log.w("determineColumns", availableSpace + "; " +mRequestedNumColumns + "; " + requestedColumnWidth +"; " + requestedHorizontalSpacing+"; "+ mNumColumns );
        return mNumColumns;
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
            outRect.top = space;
        }
    }
}
