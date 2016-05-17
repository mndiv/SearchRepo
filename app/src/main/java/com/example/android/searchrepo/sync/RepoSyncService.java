package com.example.android.searchrepo.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by KeerthanaS on 5/17/2016.
 */
public class RepoSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static RepoSyncAdapter sRepoSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("RepoSyncService", "onCreate -RepoSyncService");
        synchronized (sSyncAdapterLock) {
            if (sRepoSyncAdapter == null) {
                sRepoSyncAdapter = new RepoSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sRepoSyncAdapter.getSyncAdapterBinder();
    }
}
