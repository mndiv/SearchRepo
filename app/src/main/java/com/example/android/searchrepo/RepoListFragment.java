package com.example.android.searchrepo;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * A placeholder fragment containing a simple view.
 */
public class RepoListFragment extends Fragment {

    ArrayAdapter<String> mRepoAdapter;

    public RepoListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.repofragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Handle item selection
        switch (item.getItemId()) {
            case R.id.action_refresh:
                FetchRepoTask repoTask = new FetchRepoTask(getContext());
                repoTask.execute("stars:>1");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        String[] data = {
                "FreeCodeCamp/FreeCodeCamp",
                "twbs/bootstrap",
                "FreeCodeCamp/FreeCodeCamp",
                "twbs/bootstrap",
                "FreeCodeCamp/FreeCodeCamp",
                "twbs/bootstrap",
                "FreeCodeCamp/FreeCodeCamp",
                "twbs/bootstrap",
                "FreeCodeCamp/FreeCodeCamp",
                "twbs/bootstrap",
                "FreeCodeCamp/FreeCodeCamp",
                "twbs/bootstrap"
        };

        List<String> repoList = new ArrayList<String>(Arrays.asList(data));

        mRepoAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_repo,
                R.id.list_item_repo_textView,
                repoList);

        ListView listView = (ListView) rootView.findViewById(R.id.listitem_repo);
        listView.setAdapter(mRepoAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(),mRepoAdapter.getItem(position),Toast.LENGTH_SHORT).show();

                Intent detailIntent  = new Intent(getContext(),DetailActivity.class);
                detailIntent.putExtra(Intent.EXTRA_TEXT, mRepoAdapter.getItem(position));
                startActivity(detailIntent);
            }
        });


        return rootView;
    }

    public class FetchRepoTask extends AsyncTask<String, Void, String[]> {

        Context context;
        private final String LOG_TAG = FetchRepoTask.class.getSimpleName();

        public FetchRepoTask(Context context) {
            this.context = context;
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

        private String[] getRepoDataFromJson(String repoJsonStr)
                throws JSONException {
            // These are the names of the JSON objects that need to be extracted.
            final String OWM_ITEMS = "items";
            final String OWM_NAME = "full_name";
            final String OWM_DESCRIPTION = "description";
            final String OWM_LANGUAGE = "language";
            final String OWM_UPDATED = "updated_at";
            final String OWM_PUSHED = "pushed_at";


            JSONObject repoJson = new JSONObject(repoJsonStr);
            JSONArray repoArray = repoJson.getJSONArray(OWM_ITEMS);

            int numRepos = repoArray.length();
            String[] resultStrs = new String[numRepos];


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

                resultStrs[i] = fullName + " - " + description + " - " + language + "Updated " + str;
            }
            return resultStrs;
        }

        @Override
        protected String[] doInBackground(String... params) {

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


                String sortString = "stars";
                String order = "desc";

                Uri builtUri = Uri.parse(REPO_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(SORT_PARAM, sortString)
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
                return getRepoDataFromJson(repoJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage() + e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if(result != null){
                mRepoAdapter.clear();
                for(String repoStr : result)
                    mRepoAdapter.add(repoStr);

                // New data is back from the server.  Hooray!
            }
        }
    }

}
