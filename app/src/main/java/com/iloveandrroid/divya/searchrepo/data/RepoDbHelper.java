package com.iloveandrroid.divya.searchrepo.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.iloveandrroid.divya.searchrepo.data.RepoContract.RepoEntry;

/**
 * Created by DivyaM on 5/9/2016.
 */
public class RepoDbHelper extends SQLiteOpenHelper{

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION =2;

    static final String DATABASE_NAME = "repositories.db";

    public RepoDbHelper(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_REPO_TABLE = "CREATE TABLE " + RepoEntry.TABLE_NAME + " (" +
                // Why AutoIncrement here, and not above?
                // Unique keys will be auto-generated in either case.  But for weather
                // forecasting, it's reasonable to assume the user will want information
                // for a certain date and all dates *following*, so the forecast data
                // should be sorted accordingly.
                RepoEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                // the ID of the location entry associated with this repo data
                RepoEntry.COLUMN_FULL_NAME + " TEXT NOT NULL, " +
                RepoEntry.COLUMN_DESCRIPTION + " TEXT , " +
                RepoEntry.COLUMN_LANGUAGE + " TEXT , " +
                RepoEntry.COLUMN_PUSHED + " TEXT NOT NULL ,  " +
                RepoEntry.COLUMN_AVATAR_URL + " TEXT , " +
                RepoEntry.COLUMN_REPO_URL + " TEXT NOT NULL , " +
                RepoEntry.COLUMN_UPDATED + " TEXT NOT NULL , " +
                RepoEntry.COLUMN_CREATED + " TEXT NOT NULL , " +
                RepoEntry.COLUMN_STARCOUNT + " INTEGER , " +
                RepoEntry.COLUMN_WATCHCOUNT + " INTEGER , " +
                RepoEntry.COLUMN_FORKCOUNT + " INTEGER , " +
                RepoEntry.COLUMN_ISSUECOUNT + " INTEGER  " + " );";


        sqLiteDatabase.execSQL(SQL_CREATE_REPO_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
       // sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RepoEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);

    }
}
