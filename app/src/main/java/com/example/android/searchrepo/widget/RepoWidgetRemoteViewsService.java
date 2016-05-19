package com.example.android.searchrepo.widget;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.searchrepo.R;
import com.example.android.searchrepo.data.RepoContract;

/**
 * Created by KeerthanaS on 5/18/2016.
 */
public class RepoWidgetRemoteViewsService extends RemoteViewsService {

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

    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsService.RemoteViewsFactory() {
            private Cursor data = null;
            @Override
            public void onCreate() {
                //Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission


                final long identityToken = Binder.clearCallingIdentity();
                // Get today's data from the ContentProvider
                data = getContentResolver().query(
                        RepoContract.RepoEntry.CONTENT_URI,
                        REPO_COLUMNS,
                        null,
                        null,
                        null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_detail_list_item);

                // Extract the repo data from the Cursor
                String repoName = data.getString(INDEX_REPO_FULLNAME);
                String repoDesc = data.getString(INDEX_REPO_DESC);
                String repoLanguage = data.getString(INDEX_LANGUAGE);
                String repoUpdated = data.getString(INDEX_UPDATED);

                Log.v("RepoWidgetRemoteViews","repoName : " + repoName );
                views.setTextViewText(R.id.widget_list_item_fullName_textView, repoName);
                views.setTextViewText(R.id.widget_list_item_desc_textView, repoDesc);
                views.setTextViewText(R.id.widget_list_item_lang_textView, repoLanguage);
                views.setTextViewText(R.id.widget_list_item_update_textView, repoUpdated);

                final Intent fillInIntent = new Intent();

                Uri RepoUri = ContentUris.withAppendedId(RepoContract.RepoEntry.CONTENT_URI, position + 1);
                fillInIntent.setData(RepoUri);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;


            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
//                if (data.moveToPosition(position)) {
//                    Log.v("getItemId" , "position:" + position)
//                    return data.getLong(INDEX_REPO_ID); //To do
//                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return false;
            }


        };
    }
}
