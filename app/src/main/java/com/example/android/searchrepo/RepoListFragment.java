package com.example.android.searchrepo;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.android.searchrepo.data.RepoContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class RepoListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = RepoListFragment.class.getSimpleName();
    private static final int REPO_LOADER = 0;

    // For the Repo view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] REPO_MOSTSTARS_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name
            RepoContract.MostStarsRepoEntry.TABLE_NAME + "." + RepoContract.MostStarsRepoEntry._ID,
            RepoContract.MostStarsRepoEntry.COLUMN_FULL_NAME,
            RepoContract.MostStarsRepoEntry.COLUMN_DESCRIPTION,
            RepoContract.MostStarsRepoEntry.COLUMN_LANGUAGE,
            RepoContract.MostStarsRepoEntry.COLUMN_UPDATED
    };

    // These indices are tied to REPO_MOSTSTARS_COLUMNS.  If REPO_MOSTSTARS_COLUMNS changes, these
    // must change.
    static final int COL_REPO_MOSTSTARS_ID = 0;
    static final int COL_REPO_MOSTSTARS_FULLNAME = 1;
    static final int COL_REPO_MOSTSTARS_DESC = 2;
    static final int COL_REPO_MOSTSTARS_LANG = 3;
    static final int COL_REPO_MOSTSTARS_UPDATED = 4;

    RepoAdapter mRepoAdapter;

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
        FetchRepoTask repoTask = new FetchRepoTask(getContext());

        String language =Utility.getLanguageOption(getActivity());
        String sortOrder = Utility.getSortOption(getActivity());

        //Log.v(LOG_TAG, "sortOrder : " + sortOrder);
        repoTask.execute("stars:>1 language:" +language,sortOrder);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        // The CursorAdapter will take data from our cursor and populate the ListView.
        mRepoAdapter = new RepoAdapter(getActivity(), null, 0);

        ListView listView = (ListView) rootView.findViewById(R.id.listitem_repo);
        listView.setAdapter(mRepoAdapter);

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getContext(),mRepoAdapter.getItem(position),Toast.LENGTH_SHORT).show();
//
//                Intent detailIntent  = new Intent(getContext(),DetailActivity.class);
//                detailIntent.putExtra(Intent.EXTRA_TEXT, mRepoAdapter.getItem(position));
//                startActivity(detailIntent);
//            }
//        });


        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(REPO_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                RepoContract.MostStarsRepoEntry.CONTENT_URI,
                REPO_MOSTSTARS_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mRepoAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mRepoAdapter.swapCursor(null);

    }
}
