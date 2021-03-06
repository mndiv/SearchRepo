package com.iloveandrroid.divya.searchrepo;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.iloveandrroid.divya.searchrepo.data.RepoContract;
import com.iloveandrroid.divya.searchrepo.sync.RepoSyncAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class RepoListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = RepoListFragment.class.getSimpleName();
    private static final int REPO_LOADER = 0;
    private RepoAdapter mRepoAdapter;
    private ListView mListView;
    //private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    private static final String QUERY_KEY = "Query";
    private SearchView searchView;
    public static String mQueryText = "";

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
    private String mInitialSelectedRepo = "";


    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        void onItemSelected(Uri dateUri);
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
                updateRepositories(mQueryText);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void updateRepositories(String query) {
//        FetchRepoTask repoTask = new FetchRepoTask(getContext(),mQueryText);
//
//        String language = Utility.getLanguageOption(getActivity());
//        String sortOrder = Utility.getSortOption(getActivity());
//
//        //Log.v(LOG_TAG, "sortOrder : " + sortOrder);
//        if(query.equals("")) {
//            repoTask.execute("stars:>1 language:" + language, sortOrder);
//        }
//        else {
//            repoTask.execute(query + " language:" + language, sortOrder);
//        }

        RepoSyncAdapter.syncImmediately(getActivity());
        searchView.clearFocus();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        AdView mAdView = (AdView) rootView.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                //.addTestDevice("E73FCBCE792D2E61BFECA6E585A35374")
                .build();
        mAdView.loadAd(adRequest);


        // The CursorAdapter will take data from our cursor and populate the ListView.
        mRepoAdapter = new RepoAdapter(getActivity(), null, 0);

        mListView = (ListView) rootView.findViewById(R.id.listitem_repo);
        View emptyView = rootView.findViewById(R.id.listview_repo_empty);
        mListView.setEmptyView(emptyView);
        mListView.setAdapter(mRepoAdapter);

        searchView = (SearchView) rootView.findViewById(R.id.search);
        searchView.setIconifiedByDefault(false);
        searchView.setInputType(InputType.TYPE_CLASS_TEXT);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mQueryText = query;
                updateRepositories(mQueryText);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.v(LOG_TAG, "newText : " + newText);
                if(newText.equals("")){

                    if(!newText.equals(mQueryText)) {
                        updateRepositories(mQueryText);
                        mQueryText = "";
                    }
                    searchView.clearFocus();
                }
                return false;
            }

        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                updateRepositories(mQueryText);
                return false;
            }
        });

        //searchView.setQuery("",true);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {

//                    Intent detailIntent = new Intentads(getContext(), DetailActivity.class)
//                            .setData(ContentUris.withAppendedId(RepoContract.RepoEntry.CONTENT_URI,position+1));

                    ((Callback) getActivity())
                            .onItemSelected(ContentUris.withAppendedId(RepoContract.RepoEntry.CONTENT_URI, position + 1));

                    //startActivity(detailIntent);
                }
                //mPosition = position;
            }
        });

        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
       /* if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);

        }
*/
        if (savedInstanceState != null && savedInstanceState.containsKey(QUERY_KEY)) {
             mQueryText = savedInstanceState.getString(QUERY_KEY);
        }
        searchView.setQuery(mQueryText, true);
        //updateRepositories(mQueryText);

        return rootView;
    }


    public static String getQueryText() {
        return mQueryText;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(REPO_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public void setInitialSelectedRepo(String initialSelectedRepo) {
        mInitialSelectedRepo = initialSelectedRepo;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        /*if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);

        }*/
        outState.putString(QUERY_KEY, mQueryText);
        super.onSaveInstanceState(outState);
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
        /*if (mPosition != ListView.INVALID_POSITION) {
       // If we don't need to restart the loader, and there's a desired position to restore
        // to, do so now.
        mListView.smoothScrollToPosition(mPosition);
        }*/
        updateEmptyView();

       mListView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
           @Override
           public boolean onPreDraw() {
               if (mListView.getChildCount() > 0) {
                   mListView.getViewTreeObserver().removeOnPreDrawListener(this);

                   int position = mListView.getSelectedItemPosition();
                   if (position != ListView.INVALID_POSITION && !mInitialSelectedRepo.equals("")) {
                       Cursor data = mRepoAdapter.getCursor();
                       int count = data.getCount();
                       int name_id = data.getColumnIndex(RepoContract.RepoEntry.COLUMN_FULL_NAME);
                       for (int i = 0; i < count; i++) {
                           data.moveToPosition(i);
                           if (data.getString(name_id).equals(mInitialSelectedRepo)) {
                               position = i;
                               break;
                           }
                       }
                   }
                   if (position == ListView.INVALID_POSITION)
                       position = 0;
                   mListView.smoothScrollToPosition(position);

                   return true;
               }
            return false;
           }
       });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mRepoAdapter.swapCursor(null);

    }

    /*
        Updates the empty list view with contextually relevant information that the user can
        use to determine why they aren't seeing weather.
     */
    private void updateEmptyView() {
        if ( mRepoAdapter.getCount() == 0 ) {
            TextView tv = (TextView) getView().findViewById(R.id.listview_repo_empty);
            if ( null != tv ) {
                // if cursor is empty, why? do we have an invalid location
                int message = R.string.empty_Repo_list;
                if (!Utility.isNetworkAvailable(getActivity()) ) {
                    message = R.string.empty_repo_list_no_network;
                }
                tv.setText(message);
            }
        }
    }

    private void updateWidgets() {
        Context context = getContext();
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(RepoSyncAdapter.ACTION_DATA_UPDATED)
                .setPackage(context.getPackageName());
        context.sendBroadcast(dataUpdatedIntent);
    }

    // since we read the location when we create the loader, all we need to do is restart things
    public void onSettingsChanged() {

        updateRepositories(mQueryText);
        getLoaderManager().restartLoader(REPO_LOADER, null, this);
        updateWidgets();
    }
}
