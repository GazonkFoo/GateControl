package com.github.gazonkfoo.gatecontrol;

import android.content.Intent;
import com.google.android.gms.iid.InstanceIDListenerService;

public class GCMInstanceIDListener extends InstanceIDListenerService {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, GCMRegistration.class);
        startService(intent);
    }
}
