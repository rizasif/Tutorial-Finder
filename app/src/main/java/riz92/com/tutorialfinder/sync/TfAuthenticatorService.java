package riz92.com.tutorialfinder.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Rizwan Asif on 12/31/2014.
 */
public class TfAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private TfAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new TfAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
