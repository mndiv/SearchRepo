package com.example.android.searchrepo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class RepoListFragment extends Fragment {

    private final String LOG_TAG = RepoListFragment.class.getSimpleName();

    ArrayAdapter<String> mRepoAdapter;

    public RepoListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateRepositories();
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
                updateRepositories();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void updateRepositories() {
        FetchRepoTask repoTask = new FetchRepoTask(getContext(),mRepoAdapter);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String language = prefs.getString(getString(R.string.list_Of_Languages),
                getString(R.string.pref_language_default));

        String sortOrder = prefs.getString(getString(R.string.sortList),
                getString(R.string.pref_sort_default_MostStars));

        Log.v(LOG_TAG, "sortOrder : " + sortOrder);
        repoTask.execute("stars:>1 language:" +language,sortOrder);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        /*String[] data = {
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

        List<String> repoList = new ArrayList<String>(Arrays.asList(data));*/

        mRepoAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_repo,
                R.id.list_item_repo_textView,
                new ArrayList<String>());

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



}
