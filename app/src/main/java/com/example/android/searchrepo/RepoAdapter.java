package com.example.android.searchrepo;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by KeerthanaS on 5/10/2016.
 */
public class RepoAdapter extends CursorAdapter {

    private static final String LOG_TAG = RepoAdapter.class.getSimpleName();

    public RepoAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    private long timeStringToMilis(String time) {
        long milis = 0;
        try {
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            sd.setTimeZone(TimeZone.getTimeZone("GMT"));
            Log.d("Time zone: ", String.valueOf(sd.getTimeZone()));
            Date date = sd.parse(time);
            milis = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return milis;
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

        String result = cursor.getString(RepoListFragment.COL_REPO_MOSTSTARS_FULLNAME) + " - " +
                cursor.getString(RepoListFragment.COL_REPO_MOSTSTARS_DESC) + " - " +
                cursor.getString(RepoListFragment.COL_REPO_MOSTSTARS_LANG) +
                " Updated " + cursor.getString(RepoListFragment.COL_REPO_MOSTSTARS_UPDATED);

        return result;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_repo, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        TextView tv = (TextView)view;
        tv.setText(convertCursorRowToUXFormat(cursor));

    }
}