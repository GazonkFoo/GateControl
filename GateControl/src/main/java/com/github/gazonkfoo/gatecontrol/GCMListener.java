package com.github.gazonkfoo.gatecontrol;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
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

        boolean vibrate = sharedPreferences.getBoolean(Constants.PREF_VIBRATE, true);
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

        sharedPreferences.edit()
                .putString(Constants.GATE_STATE, stateValue)
                .putLong(Constants.MESSAGE_TIMESTAMP, newTimestamp)
                .apply();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (state == GateState.OPEN) {
            PendingIntent mainIntend = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
            PendingIntent openGateIntend = PendingIntent.getBroadcast(this, 0, new Intent(Constants.OPEN_GATE), 0);

            NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                    .setContentTitle(getResources().getString(R.string.notification_title))
                    .setContentText(getResources().getString(state.getResourceID()))
                    .setSmallIcon(R.drawable.ic_lock_open_white)
                    .setOngoing(true)
                    .setContentIntent(mainIntend)
                    .addAction(R.drawable.ic_play_arrow, getResources().getString(R.string.close_gate), openGateIntend);

            if(vibrate) {
                notification.setVibrate(new long[]{1000, 1000, 1000});
            }

            notificationManager.notify(0, notification.build());
        } else {
            notificationManager.cancel(0);
        }
    }
}
