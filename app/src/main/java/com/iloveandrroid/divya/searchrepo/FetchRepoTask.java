package com.iloveandrroid.divya.searchrepo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.util.Log;

import com.iloveandrroid.divya.searchrepo.data.RepoContract;
import com.iloveandrroid.divya.searchrepo.data.RepoContract.RepoEntry;

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
 * Created by KeerthanaS on 5/10/2016.
 */
class FetchRepoTask extends AsyncTask<String, Void, Void> {

    private final Context mContext;
    private final String mQuery;

    private final String LOG_TAG = FetchRepoTask.class.getSimpleName();

    public FetchRepoTask(Context context, String query) {
        mContext = context;
        mQuery = query;
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
        Cursor repoCursor = mContext.getContentResolver().query(
                RepoContract.RepoEntry.CONTENT_URI,
                new String[]{RepoContract.RepoEntry._ID},
                null,
                null,
                null);

        int deleted = 0;
        assert repoCursor != null;
        if (repoCursor.getCount() > 0) {
            deleted = mContext.getContentResolver().delete(RepoEntry.CONTENT_URI, null, null);
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

                    RepoValues.put(RepoEntry.COLUMN_FULL_NAME, fullName);
                    RepoValues.put(RepoEntry.COLUMN_DESCRIPTION, description);
                    RepoValues.put(RepoEntry.COLUMN_LANGUAGE, language);
                    RepoValues.put(RepoEntry.COLUMN_PUSHED, pushedStr.toString());
                    RepoValues.put(RepoEntry.COLUMN_AVATAR_URL, avatar_url);
                    RepoValues.put(RepoEntry.COLUMN_REPO_URL, html_url);
                    RepoValues.put(RepoEntry.COLUMN_UPDATED, updatedStr.toString());
                    RepoValues.put(RepoEntry.COLUMN_CREATED, createdStr.toString());
                    RepoValues.put(RepoEntry.COLUMN_STARCOUNT, stars_count);
                    RepoValues.put(RepoEntry.COLUMN_WATCHCOUNT, watch_count);
                    RepoValues.put(RepoEntry.COLUMN_FORKCOUNT, fork_count);
                    RepoValues.put(RepoEntry.COLUMN_ISSUECOUNT, issue_count);

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
                    inserted = mContext.getContentResolver().bulkInsert(RepoEntry.CONTENT_URI, cvArray);
                }

                Log.d(LOG_TAG, "FetchRepoTask Complete. " + inserted + " Inserted");

            } else {
                // Insert the new weather information into the database
                Vector<ContentValues> cVVector = new Vector<ContentValues>(1);
                ContentValues RepoValues = new ContentValues();
                RepoValues.put(RepoEntry.COLUMN_FULL_NAME, "Validation Failed");
                RepoValues.put(RepoEntry.COLUMN_DESCRIPTION, mQuery);
                RepoValues.put(RepoEntry.COLUMN_LANGUAGE, "language");
                RepoValues.put(RepoEntry.COLUMN_PUSHED, "pushed");
                RepoValues.put(RepoEntry.COLUMN_AVATAR_URL, "avatar");
                RepoValues.put(RepoEntry.COLUMN_REPO_URL, "repourl");
                RepoValues.put(RepoEntry.COLUMN_UPDATED, "updated");
                RepoValues.put(RepoEntry.COLUMN_CREATED, "created");
                RepoValues.put(RepoEntry.COLUMN_STARCOUNT, 0);
                RepoValues.put(RepoEntry.COLUMN_WATCHCOUNT, 0);
                RepoValues.put(RepoEntry.COLUMN_FORKCOUNT, 0);
                RepoValues.put(RepoEntry.COLUMN_ISSUECOUNT, 0);

                cVVector.add(RepoValues);

                int inserted = 0;
                // add to database
                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    inserted = mContext.getContentResolver().bulkInsert(RepoEntry.CONTENT_URI, cvArray);
                }
                Log.d(LOG_TAG, "FetchRepoTask Complete. " + inserted + " Inserted");
                repoCursor.close();
            }
            repoCursor.close();

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    protected Void doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;


        // Will contain the raw JSON response as a string.
        String repoJsonStr = null;


//        GithubReposClient client = new RepoSearchClient(getActivity(), username);
//        client.observable()
//                .observeOn(AndroidSchedulers.mainThread()
//                        .subscribeOn(Schedulers.io())
//                        .subscribe( ... );


        try {
// Construct the URL for the github query
// Possible parameters are avaiable at Github API page, at
// hhttps://developer.github.com/v3/search/#search-repositories


// URL url = new URL("https://api.github.com/search/repositories?q=stars:>1");

            final String REPO_BASE_URL = "https://api.github.com/search/repositories?";
            final String QUERY_PARAM = "q";
            final String SORT_PARAM = "sort";
            final String ORDER_PARAM = "order";


            String sortOrder = params[1];
            String sort = sortOrder.substring(0, sortOrder.indexOf('-'));
            String order = sortOrder.substring(sortOrder.indexOf('-') + 1);



            Log.v(LOG_TAG, "query : " + params[0] + "sort:" + sort + " order:" + order);

            Uri builtUri = Uri.parse(REPO_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, params[0])
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
                return null;
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
                return null;
            }
            repoJsonStr = buffer.toString();
            Log.v("RepoListFragment", "repoJsonString : \n" + repoJsonStr);
        } catch (IOException e) {
            Log.e("RepoListFragment", "Error ", e);
            // If the code didn't successfully get the repositories data, there's no point in attemping
            // to parse it.
            return null;
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

        return null;
    }

}