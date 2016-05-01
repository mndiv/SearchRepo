package com.example.android.searchrepo;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class RepoListFragment extends Fragment {

    ArrayAdapter<String> mRepoAdapter;
    public RepoListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);


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

        List<String> repoList  = new ArrayList<String>(Arrays.asList(data));

        mRepoAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_repo,
                R.id.list_item_repo_textView,
                repoList);

        ListView listView = (ListView)rootView.findViewById(R.id.listitem_repo);
        listView.setAdapter(mRepoAdapter);




        return rootView;
    }

    public class FetchRepoTask extends AsyncTask<Void, Void, Void>{

        private final String LOG_TAG = FetchRepoTask.class.getSimpleName();
        @Override
        protected Void doInBackground(Void... params) {

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


            try{
                // Construct the URL for the github query
                // Possible parameters are avaiable at Github API page, at
                // hhttps://developer.github.com/v3/search/#search-repositories


                URL url = new URL("https://api.github.com/search/repositories?q=stars:>1");

                // Replace this token with your actual token
                String token = "aa554744ae29e09a31af98c9e7968ea872d7f4bb";
                // Create the request to Github, and open the connection
                urlConnection = (HttpURLConnection)url.openConnection();
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
                Log.v("RepoListFragment","repoJsonString : \n" + repoJsonStr);
            }catch (IOException e){
                Log.e("RepoListFragment", "Error ", e);
                // If the code didn't successfully get the repositories data, there's no point in attemping
                // to parse it.
                return null;
            }finally {
                if(urlConnection != null){
                    urlConnection.disconnect();

                }
                if(reader != null){
                    try{
                        reader.close();
                    }catch (final IOException e){
                        Log.e("RepoListFragment", "Error closing stream", e);
                    }
                }
            }

            return null;
        }
    }
}
