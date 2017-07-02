package com.android.popularmovies.popularmovies.data;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.popularmovies.popularmovies.data.FavoriteContract.FavoriteEntry;


/**
 * Created by lei on 6/30/17.
 */

public class FavoriteProvider extends ContentProvider {
    private static final String TAG = "FavoriteProvider";

    public static final int CODE_FAVORITE = 100;
    public static final int CODE_FAVORITE_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FavoriteDBHelper mFavoriteDBHelper;

    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavoriteContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, FavoriteContract.PATH_FAVORITE, CODE_FAVORITE);
        matcher.addURI(authority, FavoriteContract.PATH_FAVORITE + "/#", CODE_FAVORITE_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mFavoriteDBHelper = new FavoriteDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        final SQLiteDatabase db = mFavoriteDBHelper.getReadableDatabase();
        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITE:
                cursor = db.query(FavoriteEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_FAVORITE_WITH_ID:
                String movieId = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{ movieId };
                cursor = db.query(FavoriteEntry.TABLE_NAME,
                        projection,
                        FavoriteEntry.COLUMN_MOVIE_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mFavoriteDBHelper.getWritableDatabase();
        long _id;
        Uri returnId;
        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITE:
                _id = db.insert(FavoriteEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnId = FavoriteEntry.buildMovieUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new android.database.SQLException("Unknown uri: " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnId;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // // TODO: 7/1/17 delete funtionality
        final SQLiteDatabase db = mFavoriteDBHelper.getWritableDatabase();
        int rowsDeleted;
        switch (sUriMatcher.match(uri)) {
            case CODE_FAVORITE_WITH_ID:
                String id = uri.getLastPathSegment();
                Log.d(TAG, "delete id: " + id);
                rowsDeleted = db.delete(FavoriteEntry.TABLE_NAME,
                        FavoriteEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
