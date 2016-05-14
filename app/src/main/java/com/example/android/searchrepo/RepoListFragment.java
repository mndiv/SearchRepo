package com.example.android.searchrepo;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.AdapterView;
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
            RepoContract.RepoEntry.TABLE_NAME + "." + RepoContract.RepoEntry._ID,
            RepoContract.RepoEntry.COLUMN_FULL_NAME,
            RepoContract.RepoEntry.COLUMN_DESCRIPTION,
            RepoContract.RepoEntry.COLUMN_LANGUAGE,
            RepoContract.RepoEntry.COLUMN_PUSHED,
            RepoContract.RepoEntry.COLUMN_AVATAR_URL,
            RepoContract.RepoEntry.COLUMN_REPO_URL,
            RepoContract.RepoEntry.COLUMN_UPDATED,
            RepoContract.RepoEntry.COLUMN_CREATED,
            RepoContract.RepoEntry.COLUMN_STARCOUNT,
            RepoContract.RepoEntry.COLUMN_WATCHCOUNT,
            RepoContract.RepoEntry.COLUMN_FORKCOUNT,
            RepoContract.RepoEntry.COLUMN_ISSUECOUNT
    };

    // These indices are tied to REPO_MOSTSTARS_COLUMNS.  If REPO_MOSTSTARS_COLUMNS changes, these
    // must change.
    static final int COL_REPO_MOSTSTARS_ID = 0;
    static final int COL_REPO_FULLNAME = 1;
    static final int COL_REPO_DESC = 2;
    static final int COL_REPO_LANG = 3;
    static final int COL_REPO_PUSHED = 4;
    static final int COL_REPO_AVATAR_URL = 5;
    static final int COL_REPO_REPO_URL = 6;
    static final int COL_REPO_UPDATED = 7;
    static final int COL_REPO_CREATED = 8;
    static final int COL_REPO_STARCOUNT = 9;
    static final int COL_REPO_WATCHCOUNT = 10;
    static final int COL_REPO_FORKCOUNT = 11;
    static final int COL_REPO_ISSUECOUNT = 12;


    RepoAdapter mRepoAdapter;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri);
    }

    public RepoListFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        updateRepositories();
//    }

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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if(cursor != null) {

//                    Intent detailIntent = new Intent(getContext(), DetailActivity.class)
//                            .setData(ContentUris.withAppendedId(RepoContract.RepoEntry.CONTENT_URI,position+1));

                    ((Callback) getActivity())
                            .onItemSelected(ContentUris.withAppendedId(RepoContract.RepoEntry.CONTENT_URI, position+1));

                    //startActivity(detailIntent);
                }
            }
        });


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
                RepoContract.RepoEntry.CONTENT_URI,
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

    // since we read the location when we create the loader, all we need to do is restart things
    public void onSettingsChanged() {

        updateRepositories();
        getLoaderManager().restartLoader(REPO_LOADER, null, this);
    }
}
