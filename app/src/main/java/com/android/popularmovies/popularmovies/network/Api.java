package com.android.popularmovies.popularmovies.network;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by lei on 6/27/17.
 */

public class Api {
    private static final String TAG = "Api";
    private static final String BASE_URL = "http://api.themoviedb.org/3/";

    private static Api sInstance;
    private TmdbService mTmdbService;
    private OkHttpClient mOkHttpClient;
    private Retrofit mRetrofit;

    public static Api instance() {
        if (sInstance == null) {
            sInstance = new Api();
        }
        return sInstance;
    }

    private Api() {
        mOkHttpClient = new OkHttpClient
                .Builder()
                .addInterceptor(logInterceptor())
                .build();
        mRetrofit = new Retrofit.Builder()
                .client(mOkHttpClient)
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private HttpLoggingInterceptor logInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d(TAG, message);
            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        return interceptor;
    }

    public TmdbService getApi() {
        if (mTmdbService == null) {
            createApi();
        }
        return mTmdbService;
    }

    private void createApi() {
        mTmdbService = mRetrofit.create(TmdbService.class);
    }
}
