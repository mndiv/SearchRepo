package com.example.android.searchrepo.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;

import com.example.android.searchrepo.R;
import com.example.android.searchrepo.RepoListFragment;
import com.example.android.searchrepo.Utility;
import com.example.android.searchrepo.data.RepoContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

/**
 * Created by KeerthanaS on 5/17/2016.
 */
public class RepoSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = RepoSyncAdapter.class.getSimpleName();
    String mQuery;

    public static final String ACTION_DATA_UPDATED =
            "com.example.android.searchrepo.app.ACTION_DATA_UPDATED";

    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    public RepoSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras,
                              String authority, ContentProviderClient provider,
                              SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");

        String language = Utility.getLanguageOption(getContext());
        String sortOrder = Utility.getSortOption(getContext());
        String query = "stars:>1 language:".concat(language);
        mQuery = RepoListFragment.getQueryText();

        Log.v(LOG_TAG, "query from repolistfragment" + mQuery);
        if (!mQuery.equals("")) {
            query = "";
            query = mQuery + " language:".concat(language);
        }


        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;


        // Will contain the raw JSON response as a string.
        String repoJsonStr = null;
        try {
            // Construct the URL for the github query
            // Possible parameters are avaiable at Github API page, at
            // hhttps://developer.github.com/v3/search/#search-repositories


            // URL url = new URL("https://api.github.com/search/repositories?q=stars:>1");

            final String REPO_BASE_URL = "https://api.github.com/search/repositories?";
            final String QUERY_PARAM = "q";
            final String SORT_PARAM = "sort";
            final String ORDER_PARAM = "order";


            String sort = sortOrder.substring(0, sortOrder.indexOf('-'));
            String order = sortOrder.substring(sortOrder.indexOf('-') + 1);


            Log.v(LOG_TAG, "query : " + query + "sort:" + sort + " order:" + order);

            Uri builtUri = Uri.parse(REPO_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, query)
                    .appendQueryParameter(SORT_PARAM, sort)
                    .appendQueryParameter(ORDER_PARAM, order)
                    .build();

            URL url = new URL(builtUri.toString());
            Log.v(LOG_TAG, "Built URI " + builtUri.toString());

            // Create the request to Github, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            repoJsonStr = buffer.toString();
            Log.v("RepoListFragment", "repoJsonString : \n" + repoJsonStr);
        } catch (IOException e) {
            Log.e("RepoListFragment", "Error ", e);
            // If the code didn't successfully get the repositories data, there's no point in attemping
            // to parse it.
            return;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();

            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("RepoListFragment", "Error closing stream", e);
                }
            }
        }

        try {
            //Log.v(LOG_TAG,"No. of repos in page = " + getRepoDataFromJson(repoJsonStr));
            getRepoDataFromJson(repoJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage() + e);
            e.printStackTrace();
        }

        return;
    }

    private long timeStringToMilis(String time) {
        long milis = 0;
        try {
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            sd.setTimeZone(TimeZone.getTimeZone("GMT"));
            Log.d("Time zone: ", String.valueOf(sd.getTimeZone()));
            Date date = sd.parse(time);
            milis = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return milis;
    }

    private void getRepoDataFromJson(String repoJsonStr)
            throws JSONException {
// These are the names of the JSON objects that need to be extracted.
        final String OWM_TOTAL_COUNT = "total_count";
        final String OWM_MESSAGE = "message";
        final String OWM_ITEMS = "items";
        final String OWM_NAME = "full_name";
        final String OWM_OWNER = "owner";
        final String OWM_AVATAR_URL = "avatar_url";
        final String OWM_DESCRIPTION = "description";
        final String OWM_LANGUAGE = "language";
        final String OWM_CREATED = "created_at";
        final String OWM_UPDATED = "updated_at";
        final String OWM_PUSHED = "pushed_at";
        final String OWM_HTML_URL = "html_url";
        final String OWM_ISSUES_COUNT = "open_issues_count";
        final String OWM_STARS_COUNT = "stargazers_count";
        final String OWM_WATCHERS_COUNT = "watchers_count";
        final String OWM_FORK_COUNT = "forks_count";

        int totalCount = 0;
        String message = null;


        // First, check if the location with this city name exists in the db
        Cursor repoCursor = getContext().getContentResolver().query(
                RepoContract.RepoEntry.CONTENT_URI,
                new String[]{RepoContract.RepoEntry._ID},
                null,
                null,
                null);

        int deleted = 0;
        assert repoCursor != null;
        if (repoCursor.getCount() > 0) {
            deleted = getContext().getContentResolver().delete(RepoContract.RepoEntry.CONTENT_URI, null, null);
            Log.v(LOG_TAG, "deleted : " + deleted);
        }


        try {
            JSONObject repoJson = new JSONObject(repoJsonStr);
            totalCount = repoJson.getInt(OWM_TOTAL_COUNT);

            Log.v(LOG_TAG, "total_count : " + totalCount);
            if (totalCount > 0) {
                JSONArray repoArray = repoJson.getJSONArray(OWM_ITEMS);

                int numRepos = repoArray.length();
                String[] resultStrs = new String[numRepos];


                // Insert the new weather information into the database
                Vector<ContentValues> cVVector = new Vector<>(numRepos);

                for (int i = 0; i < numRepos; i++) {
                    String fullName, description, language, updated, pushed;
                    String avatar_url, created, html_url;
                    int issue_count, stars_count, watch_count, fork_count;

                    JSONObject eachRepo = repoArray.getJSONObject(i);

                    fullName = eachRepo.getString(OWM_NAME);

                    JSONObject ownerObject = eachRepo.getJSONObject(OWM_OWNER);
                    avatar_url = ownerObject.getString(OWM_AVATAR_URL);

                    description = eachRepo.getString(OWM_DESCRIPTION);
                    language = eachRepo.getString(OWM_LANGUAGE);
                    html_url = eachRepo.getString(OWM_HTML_URL);
                    issue_count = eachRepo.getInt(OWM_ISSUES_COUNT);
                    stars_count = eachRepo.getInt(OWM_STARS_COUNT);
                    watch_count = eachRepo.getInt(OWM_WATCHERS_COUNT);
                    fork_count = eachRepo.getInt(OWM_FORK_COUNT);

                    updated = eachRepo.getString(OWM_UPDATED);
                    created = eachRepo.getString(OWM_CREATED);
                    pushed = eachRepo.getString(OWM_PUSHED);

                    long pushedAt = timeStringToMilis(pushed);
                    CharSequence pushedStr = DateUtils.getRelativeTimeSpanString(pushedAt,
                            System.currentTimeMillis(),
                            DateUtils.SECOND_IN_MILLIS);

                    long createdAt = timeStringToMilis(created);
                    CharSequence createdStr = DateUtils.getRelativeTimeSpanString(createdAt,
                            System.currentTimeMillis(),
                            DateUtils.SECOND_IN_MILLIS);

                    long updatedAt = timeStringToMilis(updated);
                    CharSequence updatedStr = DateUtils.getRelativeTimeSpanString(updatedAt,
                            System.currentTimeMillis(),
                            DateUtils.SECOND_IN_MILLIS);


                    ContentValues RepoValues = new ContentValues();

                    RepoValues.put(RepoContract.RepoEntry.COLUMN_FULL_NAME, fullName);
                    RepoValues.put(RepoContract.RepoEntry.COLUMN_DESCRIPTION, description);
                    RepoValues.put(RepoContract.RepoEntry.COLUMN_LANGUAGE, language);
                    RepoValues.put(RepoContract.RepoEntry.COLUMN_PUSHED, pushedStr.toString());
                    RepoValues.put(RepoContract.RepoEntry.COLUMN_AVATAR_URL, avatar_url);
                    RepoValues.put(RepoContract.RepoEntry.COLUMN_REPO_URL, html_url);
                    RepoValues.put(RepoContract.RepoEntry.COLUMN_UPDATED, updatedStr.toString());
                    RepoValues.put(RepoContract.RepoEntry.COLUMN_CREATED, createdStr.toString());
                    RepoValues.put(RepoContract.RepoEntry.COLUMN_STARCOUNT, stars_count);
                    RepoValues.put(RepoContract.RepoEntry.COLUMN_WATCHCOUNT, watch_count);
                    RepoValues.put(RepoContract.RepoEntry.COLUMN_FORKCOUNT, fork_count);
                    RepoValues.put(RepoContract.RepoEntry.COLUMN_ISSUECOUNT, issue_count);

                    cVVector.add(RepoValues);
                    resultStrs[i] = fullName + " - " + description + " - " + language + " Pushed " + pushedStr + " - " + avatar_url
                            + " - Created " + createdStr + " - Updated " + updatedStr + " - " + html_url + " - "
                            + stars_count + " - " + watch_count + " - " + fork_count + " - " + issue_count;

                }

                Log.v(LOG_TAG, "ResultStrs : " + resultStrs[0]);

                int inserted = 0;
                // add to database
                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    inserted = getContext().getContentResolver().bulkInsert(RepoContract.RepoEntry.CONTENT_URI, cvArray);
                }

                Log.d(LOG_TAG, "FetchRepoTask Complete. " + inserted + " Inserted");

            } else {
                // Insert the new weather information into the database
                Vector<ContentValues> cVVector = new Vector<ContentValues>(1);
                ContentValues RepoValues = new ContentValues();
                RepoValues.put(RepoContract.RepoEntry.COLUMN_FULL_NAME, "Validation Failed");
                RepoValues.put(RepoContract.RepoEntry.COLUMN_DESCRIPTION, mQuery);
                RepoValues.put(RepoContract.RepoEntry.COLUMN_LANGUAGE, "language");
                RepoValues.put(RepoContract.RepoEntry.COLUMN_PUSHED, "pushed");
                RepoValues.put(RepoContract.RepoEntry.COLUMN_AVATAR_URL, "avatar");
                RepoValues.put(RepoContract.RepoEntry.COLUMN_REPO_URL, "repourl");
                RepoValues.put(RepoContract.RepoEntry.COLUMN_UPDATED, "updated");
                RepoValues.put(RepoContract.RepoEntry.COLUMN_CREATED, "created");
                RepoValues.put(RepoContract.RepoEntry.COLUMN_STARCOUNT, 0);
                RepoValues.put(RepoContract.RepoEntry.COLUMN_WATCHCOUNT, 0);
                RepoValues.put(RepoContract.RepoEntry.COLUMN_FORKCOUNT, 0);
                RepoValues.put(RepoContract.RepoEntry.COLUMN_ISSUECOUNT, 0);

                cVVector.add(RepoValues);

                int inserted = 0;
                // add to database
                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    inserted = getContext().getContentResolver().bulkInsert(RepoContract.RepoEntry.CONTENT_URI, cvArray);
                }
                Log.d(LOG_TAG, "FetchRepoTask Complete. " + inserted + " Inserted");
                repoCursor.close();
                updateWidgets();
            }
            repoCursor.close();

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void updateWidgets() {
        Context context = getContext();
        // Setting the package ensures that only components in our app will receive the broadcast
                Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                .setPackage(context.getPackageName());
         context.sendBroadcast(dataUpdatedIntent);
        }
    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        RepoSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
