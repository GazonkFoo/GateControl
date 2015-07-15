package com.github.gazonkfoo.gatecontrol;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

public class GCMRegistration extends IntentService {

    private static final String NAME = GCMRegistration.class.getName();

    public GCMRegistration() {
        super(NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Intent registrationComplete = new Intent(Constants.REGISTRATION_COMPLETE);

        try {
            synchronized (NAME) {
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                Log.i(NAME, "GCM Registration Token: " + token);

                String state = new GateService(preferences).register(token);

                preferences.edit()
                        .putString(Constants.GATE_STATE, state)
                        .putLong(Constants.MESSAGE_TIMESTAMP, System.currentTimeMillis())
                        .apply();
            }
        } catch (IOException e) {
            registrationComplete.putExtra(Constants.REGISTRATION_ERROR, e);
        }

        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

}
