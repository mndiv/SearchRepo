package com.example.android.searchrepo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.searchrepo.data.RepoContract.RepoEntry;

/**
 * A placeholder fragment containing a simple view.
 */
public class RepoDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = RepoDetailFragment.class.getSimpleName();
    private String mRepoStr;
    private ShareActionProvider mShareActionProvider;
    private static final String REPO_SHARE_HASHTAG = "#GitSearchRepo";

    private static final int DETAIL_LOADER = 0;

    // For the Repo view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] REPO_MOSTSTARS_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name
            RepoEntry.TABLE_NAME + "." + RepoEntry._ID,
            RepoEntry.COLUMN_FULL_NAME,
            RepoEntry.COLUMN_DESCRIPTION,
            RepoEntry.COLUMN_LANGUAGE,
            RepoEntry.COLUMN_UPDATED
    };

    // These indices are tied to REPO_MOSTSTARS_COLUMNS.  If REPO_MOSTSTARS_COLUMNS changes, these
    // must change.
    static final int COL_REPO_MOSTSTARS_ID = 0;
    static final int COL_REPO_MOSTSTARS_FULLNAME = 1;
    static final int COL_REPO_MOSTSTARS_DESC = 2;
    static final int COL_REPO_MOSTSTARS_LANG = 3;
    static final int COL_REPO_MOSTSTARS_UPDATED = 4;

    public RepoDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.repodetailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (mRepoStr != null) {
            mShareActionProvider.setShareIntent(createShareRepoIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }

    private Intent createShareRepoIntent() {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mRepoStr + REPO_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                intent.getData(),
                REPO_MOSTSTARS_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if (!data.moveToFirst()) {
            return;
        }

        String fullName = data.getString(COL_REPO_MOSTSTARS_FULLNAME);
        String description = data.getString(COL_REPO_MOSTSTARS_DESC);
        String lang = data.getString(COL_REPO_MOSTSTARS_LANG);
        String updated = data.getString(COL_REPO_MOSTSTARS_UPDATED);


        mRepoStr = String.format("%s - %s - %s - Updated %s", fullName, description, lang, updated);

        TextView detailTextView = (TextView) getView().findViewById(R.id.repo_detail_textView);
        detailTextView.setText(mRepoStr);

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
        mShareActionProvider.setShareIntent(createShareRepoIntent());
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
