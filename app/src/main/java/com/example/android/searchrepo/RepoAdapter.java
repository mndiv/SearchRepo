package com.example.android.searchrepo;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by KeerthanaS on 5/10/2016.
 */
public class RepoAdapter extends CursorAdapter {

    private static final String LOG_TAG = RepoAdapter.class.getSimpleName();

    public RepoAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewHolder {
        public final TextView nameView;
        public final TextView descView;
        public final TextView langView;
        public final TextView updateView;

        public ViewHolder(View view) {
            nameView = (TextView) view.findViewById(R.id.list_item_fullName_textView);
            descView = (TextView) view.findViewById(R.id.list_item_desc_textView);
            langView = (TextView) view.findViewById(R.id.list_item_lang_textView);
            updateView = (TextView) view.findViewById(R.id.list_item_update_textView);
        }
    }


    /*
       This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
       string.
    */
    private String convertCursorRowToUXFormat(Cursor cursor) {
        // get row indices for our cursor
//        int idx_fullName = cursor.getColumnIndex(RepoContract.MostStarsRepoEntry.COLUMN_FULL_NAME);
//        int idx_desc = cursor.getColumnIndex(RepoContract.MostStarsRepoEntry.COLUMN_DESCRIPTION);
//        int idx_lang = cursor.getColumnIndex(RepoContract.MostStarsRepoEntry.COLUMN_LANGUAGE);
//        int idx_update = cursor.getColumnIndex(RepoContract.MostStarsRepoEntry.COLUMN_UPDATED);

        String result = cursor.getString(RepoListFragment.COL_REPO_FULLNAME) + " - " +
                cursor.getString(RepoListFragment.COL_REPO_DESC) + " - " +
                cursor.getString(RepoListFragment.COL_REPO_LANG) +
                " Updated " + cursor.getString(RepoListFragment.COL_REPO_UPDATED);

        return result;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_repo, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Read full Name of Repo from cursor
        String fullName = cursor.getString(RepoListFragment.COL_REPO_FULLNAME);
        // Read full Name of Repo from cursor
        String desc = cursor.getString(RepoListFragment.COL_REPO_DESC);
        // set name on it
        if(fullName.equals("Validation Failed")){

            viewHolder.nameView.setText("We couldn’t find any repositories matching '" + desc + "'");
            viewHolder.descView.setVisibility(View.GONE);
            viewHolder.langView.setVisibility(View.GONE);
            viewHolder.updateView.setVisibility(View.GONE);

        }else {
            viewHolder.nameView.setText(fullName);
            // set description on it
            viewHolder.descView.setText(desc);

            // Read full Name of Repo from cursor
            String lang = cursor.getString(RepoListFragment.COL_REPO_LANG);
            // set language on it
            viewHolder.langView.setText(lang);

            // Read full Name of Repo from cursor
            String updated = cursor.getString(RepoListFragment.COL_REPO_UPDATED);
            // set Updated repo on it
            viewHolder.updateView.setText("Updated " + updated);
        }
    }
}
