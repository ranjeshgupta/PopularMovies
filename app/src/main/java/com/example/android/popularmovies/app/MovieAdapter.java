package com.example.android.popularmovies.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmovies.app.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by Nish on 16-11-2015.
 */
public class MovieAdapter extends CursorRecyclerViewAdapter<MovieAdapter.MovieViewHolder>{
    private Context mContext;

    public MovieAdapter (Context context, Cursor cursor){
        super(context, cursor);
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imageView;
        private Context context;

        public MovieViewHolder(Context context, View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.image_view);

            this.context = context;
            itemView.setOnClickListener(this);
        }

        // Handles the row being being clicked
        @Override
        public void onClick(View view) {
            int position = getLayoutPosition();
            Cursor cursor = getCursor();
            if (cursor.moveToPosition(position)){
                ((MainActivityFragment.Callback) context)
                        .onItemSelected(MovieContract.MovieEntry.buildMovieDetailsWithMovieId(
                                cursor.getString(MainActivityFragment.COL_MOVIE_ID)));
            }
        }
    }

    @Override
    public MovieViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.grid_item, parent, false);
        final MovieViewHolder viewHolder = new MovieViewHolder(mContext, view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieViewHolder viewHolder, Cursor cursor) {
        Picasso.with(mContext)
                .load(cursor.getString(MainActivityFragment.COL_MOVIE_POSTER))
                .placeholder(R.drawable.ic_placeholder) // optional
                .error(R.drawable.ic_error_fallback)    // optional
                .into(viewHolder.imageView);
    }

}