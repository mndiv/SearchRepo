package com.example.android.searchrepo.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by KeerthanaS on 5/9/2016.
 */
public class RepoProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private RepoDbHelper mOpenHelper;

    static final int REPO_MOSTSTARS = 100;
    static final int REPO_MOSTSTARS_ID = 101;
    static final int REPO_FEWERSTARS = 200;
    static final int REPO_FEWERSTARS_ID = 201;
    static final int REPO_MOSTFORKS = 300;
    static final int REPO_MOSTFORKS_ID = 301;
    static final int REPO_FEWERFORKS = 400;
    static final int REPO_FEWERFORKS_ID = 401;
    static final int REPO_RECENTUPDATED = 500;
    static final int REPO_RECENTUPDATED_ID = 501;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RepoContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, RepoContract.PATH_MOSTSTARS, REPO_MOSTSTARS);
        matcher.addURI(authority, RepoContract.PATH_MOSTSTARS + "/#", REPO_MOSTSTARS_ID);

        matcher.addURI(authority, RepoContract.PATH_FEWERSTARS, REPO_FEWERSTARS);
        matcher.addURI(authority, RepoContract.PATH_FEWERSTARS + "/#", REPO_FEWERSTARS_ID); //path followed by a number

        matcher.addURI(authority, RepoContract.PATH_MOSTFORKS, REPO_MOSTFORKS);
        matcher.addURI(authority, RepoContract.PATH_MOSTFORKS + "/#", REPO_MOSTFORKS_ID); //path followed by a number

        matcher.addURI(authority, RepoContract.PATH_FEWERFORKS, REPO_FEWERFORKS);
        matcher.addURI(authority, RepoContract.PATH_FEWERFORKS + "/#", REPO_FEWERFORKS_ID); //path followed by a number

        matcher.addURI(authority, RepoContract.PATH_UPDATEDRECENTLY, REPO_RECENTUPDATED);
        matcher.addURI(authority, RepoContract.PATH_UPDATEDRECENTLY + "/#", REPO_RECENTUPDATED_ID); //path followed by a number
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new RepoDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        switch(sUriMatcher.match(uri)){
            case REPO_MOSTSTARS:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        RepoContract.MostStarsRepoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case REPO_FEWERSTARS:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        RepoContract.FewerStarsRepoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case REPO_MOSTFORKS:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        RepoContract.MostForksRepoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case REPO_FEWERFORKS:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        RepoContract.FewerForksRepoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case REPO_RECENTUPDATED:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        RepoContract.RecentUpdatedRepoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            case REPO_MOSTSTARS_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        RepoContract.MostStarsRepoEntry.TABLE_NAME,
                        projection,
                        RepoContract.MostStarsRepoEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                break;
            }
            case REPO_FEWERSTARS_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        RepoContract.FewerStarsRepoEntry.TABLE_NAME,
                        projection,
                        RepoContract.FewerStarsRepoEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                break;
            }
            case REPO_MOSTFORKS_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        RepoContract.MostForksRepoEntry.TABLE_NAME,
                        projection,
                        RepoContract.MostForksRepoEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                break;
            }
            case REPO_FEWERFORKS_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        RepoContract.FewerForksRepoEntry.TABLE_NAME,
                        projection,
                        RepoContract.FewerForksRepoEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                break;
            }
            case REPO_RECENTUPDATED_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        RepoContract.RecentUpdatedRepoEntry.TABLE_NAME,
                        projection,
                        RepoContract.RecentUpdatedRepoEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case REPO_MOSTSTARS:
                return RepoContract.MostStarsRepoEntry.CONTENT_TYPE;
            case REPO_FEWERSTARS:
                return RepoContract.FewerStarsRepoEntry.CONTENT_TYPE;

            case REPO_MOSTFORKS:
                return RepoContract.MostForksRepoEntry.CONTENT_TYPE;
            case REPO_FEWERFORKS:
                return RepoContract.FewerForksRepoEntry.CONTENT_TYPE;

            case REPO_RECENTUPDATED:
                return RepoContract.RecentUpdatedRepoEntry.CONTENT_TYPE;



            case REPO_MOSTSTARS_ID:
                return RepoContract.MostStarsRepoEntry.CONTENT_ITEM_TYPE;
            case REPO_FEWERSTARS_ID:
                return RepoContract.FewerStarsRepoEntry.CONTENT_ITEM_TYPE;

            case REPO_MOSTFORKS_ID:
                return RepoContract.MostForksRepoEntry.CONTENT_ITEM_TYPE;
            case REPO_FEWERFORKS_ID:
                return RepoContract.FewerForksRepoEntry.CONTENT_ITEM_TYPE;

            case REPO_RECENTUPDATED_ID:
                return RepoContract.RecentUpdatedRepoEntry.CONTENT_ITEM_TYPE;
            /*case FAV_MOVIES:
                return MovieContract.MoviePopularityEntry.CONTENT_TYPE;*/

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case REPO_MOSTSTARS: {
                // normalizeDate(values);
                long _id = db.insert(RepoContract.MostStarsRepoEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = RepoContract.MostStarsRepoEntry.buildMostStarsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REPO_FEWERSTARS: {
                // normalizeDate(values);
                long _id = db.insert(RepoContract.FewerStarsRepoEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = RepoContract.FewerStarsRepoEntry.buildFewerStarsUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            case REPO_MOSTFORKS: {
                // normalizeDate(values);
                long _id = db.insert(RepoContract.MostForksRepoEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = RepoContract.MostForksRepoEntry.buildMostForksUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REPO_FEWERFORKS: {
                // normalizeDate(values);
                long _id = db.insert(RepoContract.FewerForksRepoEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = RepoContract.FewerForksRepoEntry.buildFewerForksUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REPO_RECENTUPDATED: {
                // normalizeDate(values);
                long _id = db.insert(RepoContract.RecentUpdatedRepoEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = RepoContract.RecentUpdatedRepoEntry.buildRecentUpdateUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if (null == selection) selection = "1";
        switch (match) {
            case REPO_MOSTSTARS:
                rowsDeleted = db.delete(
                        RepoContract.MostStarsRepoEntry.TABLE_NAME, selection, selectionArgs);
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        RepoContract.MostStarsRepoEntry.TABLE_NAME + "'");
                break;
            case REPO_FEWERSTARS:
                rowsDeleted = db.delete(
                        RepoContract.FewerStarsRepoEntry.TABLE_NAME, selection, selectionArgs);
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        RepoContract.FewerStarsRepoEntry.TABLE_NAME + "'");
                break;

            case REPO_MOSTFORKS:
                rowsDeleted = db.delete(
                        RepoContract.MostForksRepoEntry.TABLE_NAME, selection, selectionArgs);
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        RepoContract.MostForksRepoEntry.TABLE_NAME + "'");
                break;
            case REPO_FEWERFORKS:
                rowsDeleted = db.delete(
                        RepoContract.FewerForksRepoEntry.TABLE_NAME, selection, selectionArgs);
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        RepoContract.FewerForksRepoEntry.TABLE_NAME + "'");
                break;

            case REPO_RECENTUPDATED:
                rowsDeleted = db.delete(
                        RepoContract.RecentUpdatedRepoEntry.TABLE_NAME, selection, selectionArgs);
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        RepoContract.RecentUpdatedRepoEntry.TABLE_NAME + "'");
                break;

            case REPO_MOSTSTARS_ID:
                rowsDeleted = db.delete(
                        RepoContract.MostStarsRepoEntry.TABLE_NAME,
                        RepoContract.MostStarsRepoEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        RepoContract.MostStarsRepoEntry.TABLE_NAME + "'");
                break;
            case REPO_FEWERSTARS_ID:
                rowsDeleted = db.delete(
                        RepoContract.FewerStarsRepoEntry.TABLE_NAME,
                        RepoContract.FewerStarsRepoEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        RepoContract.FewerStarsRepoEntry.TABLE_NAME + "'");
                break;

            case REPO_MOSTFORKS_ID:
                rowsDeleted = db.delete(
                        RepoContract.MostForksRepoEntry.TABLE_NAME,
                        RepoContract.MostForksRepoEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        RepoContract.MostForksRepoEntry.TABLE_NAME + "'");
                break;

            case REPO_FEWERFORKS_ID:
                rowsDeleted = db.delete(
                        RepoContract.FewerForksRepoEntry.TABLE_NAME,
                        RepoContract.FewerForksRepoEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        RepoContract.FewerForksRepoEntry.TABLE_NAME + "'");
                break;

            case REPO_RECENTUPDATED_ID:
                rowsDeleted = db.delete(
                        RepoContract.RecentUpdatedRepoEntry.TABLE_NAME,
                        RepoContract.RecentUpdatedRepoEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        RepoContract.RecentUpdatedRepoEntry.TABLE_NAME + "'");
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case REPO_MOSTSTARS:

                rowsUpdated = db.update(RepoContract.MostStarsRepoEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case REPO_FEWERSTARS:

                rowsUpdated = db.update(RepoContract.FewerStarsRepoEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case REPO_MOSTFORKS:

                rowsUpdated = db.update(RepoContract.MostForksRepoEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;

            case REPO_FEWERFORKS:

                rowsUpdated = db.update(RepoContract.FewerForksRepoEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case REPO_RECENTUPDATED:

                rowsUpdated = db.update(RepoContract.RecentUpdatedRepoEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;

            case REPO_MOSTSTARS_ID:

                rowsUpdated = db.update(RepoContract.MostStarsRepoEntry.TABLE_NAME,
                        values,
                        RepoContract.MostStarsRepoEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;


            case REPO_FEWERSTARS_ID:

                rowsUpdated = db.update(RepoContract.FewerStarsRepoEntry.TABLE_NAME,
                        values,
                        RepoContract.FewerStarsRepoEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;

            case REPO_MOSTFORKS_ID:

                rowsUpdated = db.update(RepoContract.MostForksRepoEntry.TABLE_NAME,
                        values,
                        RepoContract.MostForksRepoEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;


            case REPO_FEWERFORKS_ID:

                rowsUpdated = db.update(RepoContract.FewerForksRepoEntry.TABLE_NAME,
                        values,
                        RepoContract.FewerForksRepoEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;

            case REPO_RECENTUPDATED_ID:

                rowsUpdated = db.update(RepoContract.RecentUpdatedRepoEntry.TABLE_NAME,
                        values,
                        RepoContract.RecentUpdatedRepoEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case REPO_MOSTSTARS: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(RepoContract.MostStarsRepoEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case REPO_FEWERSTARS: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(RepoContract.FewerStarsRepoEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }

            case REPO_MOSTFORKS: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(RepoContract.MostForksRepoEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }

            case REPO_FEWERFORKS: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(RepoContract.FewerForksRepoEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }

            case REPO_RECENTUPDATED: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(RepoContract.RecentUpdatedRepoEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }

}
