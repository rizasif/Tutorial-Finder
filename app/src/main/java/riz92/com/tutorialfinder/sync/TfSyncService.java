package riz92.com.tutorialfinder.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Rizwan Asif on 12/31/2014.
 */
public class TfSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static TfSyncAdapter sTfSyncAdapter = null;

    @Override
    public void onCreate() {

        synchronized (sSyncAdapterLock) {
            if (sTfSyncAdapter == null) {
                sTfSyncAdapter = new TfSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sTfSyncAdapter.getSyncAdapterBinder();
    }
}
