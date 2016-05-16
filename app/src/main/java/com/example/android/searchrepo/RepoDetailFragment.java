package com.example.android.searchrepo;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
    public static final String DETAIL_URI = "URI";
    private Uri mUri;

    private String mRepoStr;
    private ShareActionProvider mShareActionProvider;
    private static final String REPO_SHARE_HASHTAG = "#GitSearchRepo";

    private static final int DETAIL_LOADER = 0;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private TextView descTextView;
    private  TextView repoURL;
    private TextView createdView;
    private ImageView mAvatar;

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
    private static final int COL_REPO_FULLNAME = 1;
    private static final int COL_REPO_DESC = 2;
    private static final int COL_REPO_LANG = 3;
    private static final int COL_REPO_PUSHED = 4;
    private static final int COL_REPO_AVATAR_URL = 5;
    private static final int COL_REPO_REPO_URL = 6;
    private static final int COL_REPO_UPDATED = 7;
    private static final int COL_REPO_CREATED = 8;
    private static final int COL_REPO_STARCOUNT = 9;
    private static final int COL_REPO_WATCHCOUNT = 10;
    private static final int COL_REPO_FORKCOUNT = 11;
    private static final int COL_REPO_ISSUECOUNT = 12;
    private TextView pushedView;
    private TextView updatedView;
    private TextView languageView;
    private TextView issuesCount;
    private TextView watchCount;
    private TextView starCount;
    private TextView forkCount;

    public RepoDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if(arguments != null){
            mUri = arguments.getParcelable(RepoDetailFragment.DETAIL_URI);
        }


        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout = ((CollapsingToolbarLayout) rootView.findViewById(R.id.collapsingToolbarLayout));
        mAvatar = (ImageView)rootView.findViewById(R.id.avatar_view);
        descTextView = (TextView)rootView.findViewById(R.id.detail_description);
        repoURL = (TextView)rootView.findViewById(R.id.repo_url);
       // createdView = (TextView)rootView.findViewById(R.id.created_view);
       // pushedView = (TextView)rootView.findViewById(R.id.pushed_view);
        updatedView = (TextView)rootView.findViewById(R.id.list_item_update_textView);
        languageView = (TextView)rootView.findViewById(R.id.list_item_lang_textView);
        issuesCount = (TextView)rootView.findViewById(R.id.issues_text);
        watchCount = (TextView)rootView.findViewById(R.id.watch_text);
        starCount = (TextView)rootView.findViewById(R.id.star_text);
        forkCount = (TextView)rootView.findViewById(R.id.fork_text);

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

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.

        if(null != mUri) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    REPO_MOSTSTARS_COLUMNS,
                    null,
                    null,
                    null

            );
        }
        return null;
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



        mRepoStr = String.format("%s - %s - %s - Pushed %s - %s - %s - Updated %s - %d - %d - %d - %d ",
              fullName, description, lang, pushed, avatar_url, repo_url, updated, star_count, watch_count, fork_count, issue_count);



        collapsingToolbarLayout.setTitle(fullName);

        Picasso.with(getActivity()).load(avatar_url).into(mAvatar);

        String about;
        if(description.equals("null")){
            about = "Created on" + created;
        }else{
            about = description + ", Created on " + created;
        }
        descTextView.setText(about);

        if(lang.equals("null")){
            languageView.setText("");
        }else {
            languageView.setText(lang);
        }
        updatedView.setText(pushed);

        repoURL.setText(repo_url);

        issuesCount.setText(String.valueOf(issue_count));
        watchCount.setText(String.valueOf(watch_count));
        starCount.setText(String.valueOf(star_count));
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
