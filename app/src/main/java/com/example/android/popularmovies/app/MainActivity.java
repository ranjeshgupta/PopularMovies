package com.example.android.popularmovies.app;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.Callback {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private final String DETAILFRAGMENT_TAG = "DFTAG";
    private String mSorting;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //mSorting = Utility.getPreferredSorting(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailActivity.DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
            else {
                mTwoPane = false;
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sorting) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String sorting = Utility.getPreferredSorting(this);
        Log.w(LOG_TAG, mSorting + ";" + sorting);
        if (sorting != null && !sorting.equals(mSorting)) {
            MainActivityFragment mf = (MainActivityFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_main);
            if ( null != mf ) {
                mf.onSortingChanged();
            }

            DetailActivity.DetailFragment df = (DetailActivity.DetailFragment)getSupportFragmentManager()
                    .findFragmentByTag(DETAILFRAGMENT_TAG);
            if ( null != df ) {
                df.getFragmentManager().beginTransaction().detach(df).commit();

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailActivity.DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }

            mSorting = sorting;
        }
    }

    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailActivity.DetailFragment.DETAIL_URI, contentUri);

            DetailActivity.DetailFragment fragment = new DetailActivity.DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
    }
}