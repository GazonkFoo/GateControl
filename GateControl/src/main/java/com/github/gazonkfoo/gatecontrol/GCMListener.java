package com.github.gazonkfoo.gatecontrol;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.gcm.GcmListenerService;

public class GCMListener extends GcmListenerService {

    private static final String NAME = GCMListener.class.getName();

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        long oldTimestamp = sharedPreferences.getLong(Constants.MESSAGE_TIMESTAMP, 0L);

        long newTimestamp;
        try {
            newTimestamp = Long.parseLong(data.getString("timestamp"));
        } catch (NumberFormatException e) {
            newTimestamp = System.currentTimeMillis();
            Log.w(NAME, "Invalid timestamp received: " + e.getMessage(), e);
        }

        if (oldTimestamp > newTimestamp)
            return;

        String stateValue = data.getString("message");
        GateState state = GateState.fromValue(stateValue);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (state == GateState.OPEN) {
            NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                    .setContentTitle(getResources().getString(R.string.notification_title))
                    .setContentText(getResources().getString(state.getResourceID()))
                    .setVibrate(new long[]{1000, 1000, 1000})
                    .setSmallIcon(R.drawable.ic_lock_open_white)
                    .setOngoing(true);
            notificationManager.notify(0, notification.build());
        } else {
            notificationManager.cancel(0);
        }

        sharedPreferences.edit()
                .putString(Constants.GATE_STATE, stateValue)
                .putLong(Constants.MESSAGE_TIMESTAMP, newTimestamp)
                .apply();
    }
}
