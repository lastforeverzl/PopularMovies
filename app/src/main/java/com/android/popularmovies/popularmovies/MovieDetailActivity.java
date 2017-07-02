package com.android.popularmovies.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.android.popularmovies.popularmovies.data.FavoriteContract.FavoriteEntry;
import com.android.popularmovies.popularmovies.databinding.ActivityDetailBinding;
import com.android.popularmovies.popularmovies.entity.Movie;
import com.android.popularmovies.popularmovies.entity.Review;
import com.android.popularmovies.popularmovies.entity.ReviewsResponse;
import com.android.popularmovies.popularmovies.entity.Video;
import com.android.popularmovies.popularmovies.entity.VideosResponse;
import com.android.popularmovies.popularmovies.network.NetworkUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lei on 6/15/17.
 */

public class MovieDetailActivity extends AppCompatActivity implements
        Callback<Object>,
        TrailersAdapter.TrailersAdapterOnClickHandler {
    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    private static final String EXTRA_MOVIE_DETAIL = "com.android.popularmovies.movie_detail";
    private static final String REVIEW_PAGE = "1";

    private static final String BUNDLE_SCROLLVIEW_LAYOUT = "MovieDetailActivity.scrollview.layout";

    public static final String[] CHECK_FAVORITE_PROJECTION = {
            FavoriteEntry.COLUMN_MOVIE_ID,
            FavoriteEntry.COLUMN_MOVIE_ORIGINAL_TITLE
    };

    private Movie mMovie;

    private ActivityDetailBinding mActivityDetailBinding;

    private RecyclerView trailersRecyclerView;
    private TrailersAdapter mTrailersAdapter;
    private NestedScrollView mNestedScrollView;
    private RecyclerView reviewsRecyclerView;
    private ReviewsAdapter mReviewsAdapter;

    private boolean isInFavorite = false;

    private int[] scrollViewPosition;

    public static Intent newIntent(Context context, Movie movie) {
        Intent i = new Intent(context, MovieDetailActivity.class);
        i.putExtra(EXTRA_MOVIE_DETAIL, movie);
        return i;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        mNestedScrollView = (NestedScrollView) findViewById(R.id.nested_scroll_view);

        setupRecyclerView();

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_MOVIE_DETAIL)) {
            mMovie = intent.getParcelableExtra(EXTRA_MOVIE_DETAIL);
        }
        mActivityDetailBinding.setMovie(mMovie);
        checkFavorite(this);
        loadData();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(BUNDLE_SCROLLVIEW_LAYOUT,
                new int[]{ mNestedScrollView.getScrollX(), mNestedScrollView.getScrollY()});
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            scrollViewPosition = savedInstanceState.getIntArray(BUNDLE_SCROLLVIEW_LAYOUT);
        }
    }

    @Override
    public void onResponse(Call<Object> call, Response<Object> response) {
        Object responseBody = response.body();
        if (responseBody instanceof VideosResponse) {
            VideosResponse results = (VideosResponse) responseBody;
            List<Video> videos = results.getResults();
            mTrailersAdapter.setVideos(videos);
        } else if (responseBody instanceof ReviewsResponse) {
            ReviewsResponse results = (ReviewsResponse) responseBody;
            List<Review> reviews;
            if (results.getResults().size() != 0) {
                reviews = results.getResults();
                mReviewsAdapter.setReviews(reviews);
            } else {
                Log.d(TAG, "No Reviews");
            }
        }
        scrollToSavedPosition();
    }

    @Override
    public void onFailure(Call<Object> call, Throwable t) {
        Log.d(TAG, t.getMessage());
    }

    private void setupRecyclerView() {
        trailersRecyclerView = (RecyclerView) findViewById(R.id.rv_trailers);
        trailersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mTrailersAdapter = new TrailersAdapter(MovieDetailActivity.this, this);

        reviewsRecyclerView = (RecyclerView) findViewById(R.id.rv_reviews);
        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mReviewsAdapter = new ReviewsAdapter(MovieDetailActivity.this);
    }

    private void loadData() {
        Call videosCall = NetworkUtils.loadMovieVideos(mMovie.getId());
        videosCall.enqueue(this);
        trailersRecyclerView.setAdapter(mTrailersAdapter);

        Call reviewsCall = NetworkUtils.loadMovieReviews(mMovie.getId(), REVIEW_PAGE);
        reviewsCall.enqueue(this);
        reviewsRecyclerView.setAdapter(mReviewsAdapter);
    }

    private void scrollToSavedPosition() {
        if (scrollViewPosition != null) {
            mNestedScrollView.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "in run");
                    mNestedScrollView.scrollTo(scrollViewPosition[0], scrollViewPosition[1]);
                }
            });
        }
    }

    @Override
    public void onClick(String key) {
        String video = "https://www.youtube.com/watch?v=" + key;
        Intent openVideo = new Intent(Intent.ACTION_VIEW);
        openVideo.setData(Uri.parse(video));
        if (openVideo.resolveActivity(getPackageManager()) != null) {
            startActivity(openVideo);
        }
    }

    public void addFavorite(View view) {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);
        if (isInFavorite) {
            view.startAnimation(anim);
            removeFromFavorite();
            Uri deleteUri = FavoriteEntry.buildMovieUri(mMovie.getId());
            Log.d(TAG, deleteUri.toString());
            getContentResolver().delete(deleteUri, null, null);
            Snackbar.make(mNestedScrollView, getResources().getString(R.string.remove_from_favorite), Snackbar.LENGTH_SHORT).show();
        } else {
            view.startAnimation(anim);
            addToFavorite();
            ContentValues contentValues = new ContentValues();
            contentValues.put(FavoriteEntry.COLUMN_MOVIE_ID, mMovie.getId());
            contentValues.put(FavoriteEntry.COLUMN_MOVIE_ORIGINAL_TITLE, mMovie.getOriginalTitle());
            contentValues.put(FavoriteEntry.COLUMN_MOVIE_POSTER_PATH, mMovie.getPosterPath());
            contentValues.put(FavoriteEntry.COLUMN_MOVIE_RELEASE_DATE, mMovie.getReleaseDate());
            contentValues.put(FavoriteEntry.COLUMN_MOVIE_TITLE, mMovie.getTitle());
            contentValues.put(FavoriteEntry.COLUMN_MOVIE_VOTE_AVERAGE, mMovie.getVoteAverage());
            contentValues.put(FavoriteEntry.COLUMN_MOVIE_OVERVIEW, mMovie.getOverview());
            Uri uri = getContentResolver().insert(FavoriteEntry.CONTENT_URI, contentValues);

            if (uri != null) {
                Snackbar.make(mNestedScrollView, getResources().getString(R.string.add_to_favorite), Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void checkFavorite(final Context context) {
        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {
                Uri movieUri = FavoriteEntry.buildMovieUri(mMovie.getId());
                Cursor movieCursor = context.getContentResolver().query(movieUri,
                        CHECK_FAVORITE_PROJECTION,
                        null,
                        null,
                        null);
                if (movieCursor == null || movieCursor.getCount() == 0) {
                    removeFromFavorite();
                } else {
                    addToFavorite();
                }
                movieCursor.close();
            }
        });
        checkForEmpty.start();
    }

    private void removeFromFavorite() {
        mActivityDetailBinding.btAddFavorite.setText(getString(R.string.add_button));
        isInFavorite = false;
    }

    private void addToFavorite() {
        mActivityDetailBinding.btAddFavorite.setText(getString(R.string.remove_button));
        isInFavorite = true;
    }
}
