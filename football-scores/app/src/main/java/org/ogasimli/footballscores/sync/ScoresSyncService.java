package org.ogasimli.footballscores.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by com.ogasimli on 11.10.2015.
 */
public class ScoresSyncService extends Service{

    private static ScoresSyncAdapter mSyncAdapter = null;

    private static final Object sSyncAdapterLock = new Object();

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (mSyncAdapter == null) {
                mSyncAdapter = new ScoresSyncAdapter(getApplicationContext());
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mSyncAdapter.getSyncAdapterBinder();
    }
}
