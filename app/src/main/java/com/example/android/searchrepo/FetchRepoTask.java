package com.example.android.searchrepo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.format.DateUtils;
import android.util.Log;

import com.example.android.searchrepo.data.RepoContract;
import com.example.android.searchrepo.data.RepoContract.RepoEntry;

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
public class FetchRepoTask extends AsyncTask<String, Void, Void> {

    private final Context mContext;

    private final String LOG_TAG = FetchRepoTask.class.getSimpleName();

    public FetchRepoTask(Context context) {
        mContext = context;
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
        final String OWM_ITEMS = "items";
        final String OWM_NAME = "full_name";
        final String OWM_DESCRIPTION = "description";
        final String OWM_LANGUAGE = "language";
        final String OWM_UPDATED = "updated_at";
        final String OWM_PUSHED = "pushed_at";

        try {
            JSONObject repoJson = new JSONObject(repoJsonStr);
            JSONArray repoArray = repoJson.getJSONArray(OWM_ITEMS);

            int numRepos = repoArray.length();
            String[] resultStrs = new String[numRepos];


            // Insert the new weather information into the database
            Vector<ContentValues> cVVector = new Vector<ContentValues>(numRepos);

            for (int i = 0; i < numRepos; i++) {
                String fullName, description, language, updated, pushed;

                JSONObject eachRepo = repoArray.getJSONObject(i);
                fullName = eachRepo.getString(OWM_NAME);
                description = eachRepo.getString(OWM_DESCRIPTION);
                language = eachRepo.getString(OWM_LANGUAGE);
                //updated = eachRepo.getString(OWM_UPDATED);
                pushed = eachRepo.getString(OWM_PUSHED);

                long pushedAt = timeStringToMilis(pushed);

                CharSequence str = DateUtils.getRelativeTimeSpanString(pushedAt,
                        System.currentTimeMillis(),
                        DateUtils.SECOND_IN_MILLIS);


                ContentValues RepoValues = new ContentValues();

                RepoValues.put(RepoEntry.COLUMN_FULL_NAME, fullName);
                RepoValues.put(RepoEntry.COLUMN_DESCRIPTION, description);
                RepoValues.put(RepoEntry.COLUMN_LANGUAGE, language);
                RepoValues.put(RepoEntry.COLUMN_UPDATED, str.toString());
                cVVector.add(RepoValues);
                resultStrs[i] = fullName + " - " + description + " - " + language + " Updated " + str;


            }

            // First, check if the location with this city name exists in the db
            Cursor repoCursor = mContext.getContentResolver().query(
                    RepoContract.RepoEntry.CONTENT_URI,
                    new String[]{RepoContract.RepoEntry._ID},
                    null,
                    null,
                    null);

            int deleted = 0;
            if(repoCursor.getCount()>0){
                deleted = mContext.getContentResolver().delete(RepoEntry.CONTENT_URI, null,null);
                Log.v(LOG_TAG, "deleted : " + deleted);
            }

            int inserted = 0;
            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(RepoEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchRepoTask Complete. " + inserted + " Inserted");

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


            Log.v(LOG_TAG, "sort:" + sort + " order:" + order);

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
                buffer.append(line + "\n");
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