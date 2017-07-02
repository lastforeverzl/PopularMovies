package com.android.popularmovies.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.popularmovies.popularmovies.data.FavoriteContract;
import com.android.popularmovies.popularmovies.entity.Movie;
import com.android.popularmovies.popularmovies.entity.MoviesResponse;
import com.android.popularmovies.popularmovies.network.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements
        Callback<MoviesResponse>,
        LoaderManager.LoaderCallbacks<Cursor>,
        MoviesAdapter.MoviesAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String POPULAR_MOVIES = "popular_movies";
    private static final String TOP_RATED_MOVIES = "top_rated_movies";
    private static final String FAVORITE_MOVIES = "favorite_movies";
    private static final int ID_MOVIES_LOADER = 41;

    private static final String BUNDLE_RECYCLER_LAYOUT = "MainActivity.recycler.layout";
    private static final String BUNDLE_MOVIE_LIST = "MainActivity.recycler.moiveList";
    private static final String BUNDLE_CURRENT_PAGE = "MainActivity.currentPage";
    private static final String BUNDLE_MAX_PAGE = "MainActivity.maxPage";
    private static final String BUNDLE_SORT_CRITERIA = "MainActivity.SortCriteria";

    public static final String[] MOVIES_QUERY_PROJECTION = {
            FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID,
            FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ORIGINAL_TITLE,
            FavoriteContract.FavoriteEntry.COLUMN_MOVIE_TITLE,
            FavoriteContract.FavoriteEntry.COLUMN_MOVIE_OVERVIEW,
            FavoriteContract.FavoriteEntry.COLUMN_MOVIE_VOTE_AVERAGE,
            FavoriteContract.FavoriteEntry.COLUMN_MOVIE_POSTER_PATH,
            FavoriteContract.FavoriteEntry.COLUMN_MOVIE_RELEASE_DATE
    };

    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_MOVIE_ORIGINAL_TITLE = 1;
    public static final int INDEX_MOVIE_TITLE = 2;
    public static final int INDEX_MOVIE_OVERVIEW = 3;
    public static final int INDEX_MOVIE_VOTE_AVERAGE = 4;
    public static final int INDEX_MOVIE_POSTER_PATH = 5;
    public static final int INDEX_MOVIE_RELEASE_DATE = 6;

    private FrameLayout mFrameLayout;
    private RecyclerView mRecyclerView;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private MoviesAdapter mMoviesAdapter;

    private String sortCriteria = POPULAR_MOVIES;

    private boolean isLoading = false;
    private int currentPage = 1;
    private static int maxPage = Integer.MAX_VALUE;

    private List<Movie> mFavoriteMovieList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFrameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_posters);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        setupRecyclerView();
        if (savedInstanceState == null) {
            loadData(POPULAR_MOVIES);
        } else {
            loadDataFromBundle(savedInstanceState);
        }
        getSupportLoaderManager().initLoader(ID_MOVIES_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_popular:
                sortCriteria = POPULAR_MOVIES;
                loadData(POPULAR_MOVIES);
                return true;
            case R.id.action_top_rated:
                sortCriteria = TOP_RATED_MOVIES;
                loadData(TOP_RATED_MOVIES);
                return true;
            case R.id.action_favorite:
                sortCriteria = FAVORITE_MOVIES;
                queryDB();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(Movie movie) {
        Intent intent = MovieDetailActivity.newIntent(MainActivity.this, movie);
        startActivity(intent);
    }

    private void loadData(String order) {
        currentPage = 1;
        mLoadingIndicator.setVisibility(View.VISIBLE);
        loadMoreData(order);
        scrollToTop();
    }

    private void loadMoreData(String order) {
        showDataView();
        if (order.equals(POPULAR_MOVIES)) {
            Call<MoviesResponse> call = NetworkUtils.loadPopular(String.valueOf(currentPage));
            call.enqueue(this);
        } else if (order.equals(TOP_RATED_MOVIES)) {
            Call<MoviesResponse> call = NetworkUtils.loadTopRated(String.valueOf(currentPage));
            call.enqueue(this);
        }
    }

    private void loadDataFromBundle(Bundle savedInstanceState) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        List<Movie> movies = savedInstanceState.getParcelableArrayList(BUNDLE_MOVIE_LIST);
        mMoviesAdapter.setMovieList(movies);
        currentPage = savedInstanceState.getInt(BUNDLE_CURRENT_PAGE);
        maxPage = savedInstanceState.getInt(BUNDLE_MAX_PAGE);
        sortCriteria = savedInstanceState.getString(BUNDLE_SORT_CRITERIA);
    }

    private void queryDB() {
        mMoviesAdapter.setMovieList(mFavoriteMovieList);
    }

    @Override
    public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
        MoviesResponse results = response.body();
        List<Movie> movies = results.getResults();
        maxPage = results.getTotalPages();

        mLoadingIndicator.setVisibility(View.INVISIBLE);
        if (movies != null) {
            if (currentPage == 1)
                mMoviesAdapter.setMovieList(movies);
            else
                mMoviesAdapter.updateMovieList(movies);
            currentPage++;
        } else {
            showErrorMessage();
        }
        isLoading = false;
    }

    @Override
    public void onFailure(Call<MoviesResponse> call, Throwable t) {
        Log.d(TAG, t.getMessage());
    }

    private void showDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void scrollToTop() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.scrollToPositionWithOffset(0, 0);
    }

    private void setupRecyclerView() {
        GridLayoutManager gridLayoutManager
                = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMoviesAdapter = new MoviesAdapter(MainActivity.this, this);
        mRecyclerView.setAdapter(mMoviesAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                if (lastVisibleItem >= totalItemCount - 2 && dy > 0 && !isLoading) {
                    if (currentPage <= maxPage) {
                        isLoading = true;
                        loadMoreData(sortCriteria);
                    } else {
                        showLastPageMesage();
                    }
                }
            }
        });
    }

    private void showLastPageMesage() {
        Snackbar.make(mFrameLayout, getResources().getString(R.string.the_last_page_message), Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ID_MOVIES_LOADER:
                Uri  moviesQueryUri = FavoriteContract.FavoriteEntry.CONTENT_URI;
                return new CursorLoader(this,
                        moviesQueryUri,
                        MOVIES_QUERY_PROJECTION,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mFavoriteMovieList.clear();
        data.moveToFirst();
        while (!data.isAfterLast()) {
            Log.d(TAG, "cursor: " + data.getInt(INDEX_MOVIE_ID) + ", " + data.getString(INDEX_MOVIE_ORIGINAL_TITLE));
            Movie movie = new Movie();
            movie.setId(data.getInt(INDEX_MOVIE_ID));
            movie.setTitle(data.getString(INDEX_MOVIE_TITLE));
            movie.setOriginalTitle(data.getString(INDEX_MOVIE_ORIGINAL_TITLE));
            movie.setOverview(data.getString(INDEX_MOVIE_OVERVIEW));
            movie.setVoteAverage(Double.valueOf(data.getString(INDEX_MOVIE_VOTE_AVERAGE)));
            movie.setPosterPath(data.getString(INDEX_MOVIE_POSTER_PATH));
            movie.setReleaseDate(data.getString(INDEX_MOVIE_RELEASE_DATE));
            mFavoriteMovieList.add(movie);
            data.moveToNext();
        }
        if (sortCriteria.equals(FAVORITE_MOVIES)) {
            mMoviesAdapter.setMovieList(mFavoriteMovieList);
        }
        Log.d(TAG, "cursor finished");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, mRecyclerView.getLayoutManager().onSaveInstanceState());
        outState.putParcelableArrayList(BUNDLE_MOVIE_LIST, mMoviesAdapter.getMovieList());
        outState.putInt(BUNDLE_CURRENT_PAGE, currentPage);
        outState.putInt(BUNDLE_MAX_PAGE, maxPage);
        outState.putString(BUNDLE_SORT_CRITERIA, sortCriteria);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "in onLoaderReset");
    }
}
