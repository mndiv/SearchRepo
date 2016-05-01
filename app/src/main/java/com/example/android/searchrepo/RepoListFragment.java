package com.example.android.searchrepo;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
}
