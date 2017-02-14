package com.iloveandrroid.divya.searchrepo.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;

import com.iloveandrroid.divya.searchrepo.MainActivity;
import com.iloveandrroid.divya.searchrepo.R;
import com.iloveandrroid.divya.searchrepo.data.RepoContract;

/**
 * Created by KeerthanaS on 5/18/2016.
 */
public class RepoWidgetIntentService extends IntentService {

    private static final String[] REPO_COLUMNS = {
            RepoContract.RepoEntry.COLUMN_FULL_NAME,
            RepoContract.RepoEntry.COLUMN_DESCRIPTION,
            RepoContract.RepoEntry.COLUMN_LANGUAGE,
            RepoContract.RepoEntry.COLUMN_UPDATED
    };
    // these indices must match the projection
    private static final int INDEX_REPO_FULLNAME = 0;
    private static final int INDEX_REPO_DESC = 1;
    private static final int INDEX_LANGUAGE = 2;
    private static final int INDEX_UPDATED = 3;

    public RepoWidgetIntentService() {
        super("RepoWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Retrieve all of the Today widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                com.iloveandrroid.divya.searchrepo.widget.RepoWidgetProvider.class));

        // Get today's data from the ContentProvider
        //String location = Utility.getPreferredLocation(this);
        // First, check if the location with this city name exists in the db
        Cursor data = getContentResolver().query(
                RepoContract.RepoEntry.CONTENT_URI,
                REPO_COLUMNS,
                null,
                null,
                null);

        if (data == null) {
            return;
        }
        if (!data.moveToFirst()) {
            data.close();
            return;
        }

        // Extract the repo data from the Cursor
        String repoName = data.getString(INDEX_REPO_FULLNAME);
        String repoDesc = data.getString(INDEX_REPO_DESC);
        String repoLanguage = data.getString(INDEX_LANGUAGE);
        String repoUpdated = data.getString(INDEX_UPDATED);

        Log.v("repoWidgetIntentService", "repoName:" + repoName + "\trepoDesc:" + repoDesc +
                "\trepoLang:" + repoLanguage + "\trepoUpdated;" +repoUpdated);

        data.close();

        // Perform this loop procedure for each Today widget
        for (int appWidgetId : appWidgetIds) {
            int layoutId = R.layout.widget_stars_repo;
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            views.setTextViewText(R.id.widget_repo_name, repoName);
            views.setTextViewText(R.id.widget_repo_desc, repoDesc);
//            views.setTextViewText(R.id.widget_repo_language, repoLanguage);
//            views.setTextViewText(R.id.widget_repo_updated, repoUpdated);

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

}