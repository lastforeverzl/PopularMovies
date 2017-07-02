package com.android.popularmovies.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.popularmovies.popularmovies.data.FavoriteContract.FavoriteEntry;

/**
 * Created by lei on 6/30/17.
 */

public class FavoriteDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "favorite.db";

    private static final int DATABASE_VERSION = 3;

    public FavoriteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String SQL_CREATE_FAVORITE_TABLE =
            "CREATE TABLE " + FavoriteEntry.TABLE_NAME + " (" +
                    FavoriteEntry._ID                         + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FavoriteEntry.COLUMN_MOVIE_ID             + " INTEGER NOT NULL, "                 +
                    FavoriteEntry.COLUMN_MOVIE_ORIGINAL_TITLE + " TEXT NOT NULL,"                     +
                    FavoriteEntry.COLUMN_MOVIE_TITLE          + " TEXT NOT NULL, "                    +
                    FavoriteEntry.COLUMN_MOVIE_POSTER_PATH    + " TEXT NOT NULL, "                    +
                    FavoriteEntry.COLUMN_MOVIE_RELEASE_DATE   + " TEXT NOT NULL, "                    +
                    FavoriteEntry.COLUMN_MOVIE_VOTE_AVERAGE   + " TEXT NOT NULL, "                    +
                    FavoriteEntry.COLUMN_MOVIE_OVERVIEW       + " TEXT, "                             +
                    " UNIQUE (" + FavoriteEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

    private static final String DATABASE_ALTER_FAVORITE_2 = "ALTER TABLE "
            + FavoriteEntry.TABLE_NAME + " ADD COLUMN " + FavoriteEntry.COLUMN_MOVIE_OVERVIEW  + " TEXT;";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL(DATABASE_ALTER_FAVORITE_2);
        }
    }
}
