package com.example.android.searchrepo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.searchrepo.data.RepoContract.RepoEntry;
import com.squareup.picasso.Picasso;

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
            RepoEntry.COLUMN_PUSHED,
            RepoEntry.COLUMN_AVATAR_URL,
            RepoEntry.COLUMN_REPO_URL,
            RepoEntry.COLUMN_UPDATED,
            RepoEntry.COLUMN_CREATED,
            RepoEntry.COLUMN_STARCOUNT,
            RepoEntry.COLUMN_WATCHCOUNT,
            RepoEntry.COLUMN_FORKCOUNT,
            RepoEntry.COLUMN_ISSUECOUNT
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

    public RepoDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        String fullName = data.getString(COL_REPO_FULLNAME);
        String description = data.getString(COL_REPO_DESC);
        String lang = data.getString(COL_REPO_LANG);
        String pushed = data.getString(COL_REPO_PUSHED);
        String avatar_url = data.getString(COL_REPO_AVATAR_URL);
        String repo_url = data.getString(COL_REPO_REPO_URL);
        String updated = data.getString(COL_REPO_UPDATED);
        String created = data.getString(COL_REPO_CREATED);
        int star_count = data.getInt(COL_REPO_STARCOUNT);
        int watch_count = data.getInt(COL_REPO_WATCHCOUNT);
        int fork_count = data.getInt(COL_REPO_FORKCOUNT);
        int issue_count = data.getInt(COL_REPO_ISSUECOUNT);


       // mRepoStr = String.format("%s - %s - %s - Pushed %s - %s - %s - Updated %s - %d - %d - %d - %d ",
         //       fullName, description, lang, pushed, avatar_url, repo_url, updated, star_count, watch_count, fork_count, issue_count);

//        TextView detailTextView = (TextView) getView().findViewById(R.id.repo_detail_textView);
//        detailTextView.setText(mRepoStr);

        CollapsingToolbarLayout collapsingToolbarLayout = ((CollapsingToolbarLayout) getView().findViewById(R.id.collapsingToolbarLayout));
        collapsingToolbarLayout.setTitle(fullName);

        Picasso.with(getActivity()).load(avatar_url).into((ImageView) getView().findViewById(R.id.avatar_view));

        TextView descTextView = (TextView) getView().findViewById(R.id.detail_description);
        descTextView.setText(description);

        TextView repoURL = (TextView) getView().findViewById(R.id.repo_url);
        repoURL.setText(repo_url);


        TextView createdView = (TextView) getView().findViewById(R.id.created_view);
        createdView.setText(created);

        TextView pushedView = (TextView) getView().findViewById(R.id.pushed_view);
        pushedView.setText(pushed);

        TextView updatedView = (TextView) getView().findViewById(R.id.updated_view);
        updatedView.setText(updated);

        TextView languageView = (TextView) getView().findViewById(R.id.lang_textView);
        languageView.setText(lang);

        //Button issuesbtn = (Button)getView().findViewById(R.id.buttonissues);

        TextView issuesCount = (TextView) getView().findViewById(R.id.issues_text);
        issuesCount.setText(String.valueOf(issue_count));

        TextView watchCount = (TextView) getView().findViewById(R.id.watch_text);
        watchCount.setText(String.valueOf(watch_count));

        TextView starCount = (TextView) getView().findViewById(R.id.star_text);
        starCount.setText(String.valueOf(star_count));

        TextView forkCount = (TextView) getView().findViewById(R.id.fork_text);
        forkCount.setText(String.valueOf(fork_count));


        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareRepoIntent());
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
