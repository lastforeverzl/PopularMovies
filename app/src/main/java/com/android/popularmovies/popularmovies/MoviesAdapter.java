package com.android.popularmovies.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.popularmovies.popularmovies.entity.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lei on 6/15/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {
    private static final String TAG = "MoviesAdapter";

    private Context mContext;
    private MoviesAdapterOnClickHandler mClickHandler;
    private List<Movie> mMovieList;

    public interface MoviesAdapterOnClickHandler {
        void onClick(Movie movie);
    }

    public MoviesAdapter(Context context, MoviesAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
        mMovieList = new ArrayList<>();
    }

    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.movie_list_item, parent, false);
        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesViewHolder holder, int position) {
        Movie movie = mMovieList.get(position);
        Picasso.with(mContext).load(movie.getPosterUri()).fit().into(holder.mPoster);
    }

    @Override
    public int getItemCount() {
        if (mMovieList == null)
            return 0;
        return mMovieList.size();
    }

    public ArrayList<Movie> getMovieList() {
        return (ArrayList<Movie>) mMovieList;
    }

    public void setMovieList(List<Movie> list) {
        if (mMovieList != null) {
            mMovieList.clear();
        }
        mMovieList.addAll(list);
        notifyDataSetChanged();
    }

    public void updateMovieList(List<Movie> list) {
        if (mMovieList != null) {
            mMovieList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public class MoviesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView mPoster;

        public MoviesViewHolder(View itemView) {
            super(itemView);
            mPoster = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            Movie movie = mMovieList.get(adapterPosition);
            mClickHandler.onClick(movie);
        }
    }
}
