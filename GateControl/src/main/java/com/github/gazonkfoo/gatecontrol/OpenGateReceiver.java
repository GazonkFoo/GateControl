package com.github.gazonkfoo.gatecontrol;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.IOException;

public class OpenGateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final Context appContext = context.getApplicationContext();
        new AsyncTask<Void, Void, Void>() {
            protected Void doInBackground(Void... params) {
                try {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
                    new GateService(preferences).buttonDown();
                } catch (IOException e) {
                    Toast.makeText(appContext, e.getMessage(), Toast.LENGTH_LONG).show();
                }
                return null;
            }
        }.execute();
    }

}
