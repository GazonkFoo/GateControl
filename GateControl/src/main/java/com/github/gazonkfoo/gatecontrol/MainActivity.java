package com.github.gazonkfoo.gatecontrol;

import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private BroadcastReceiver registrationReceiver;

    private ProgressBar registrationProgressBar;
    private ImageView stateImage;
    private TextView stateText;
    private FloatingActionButton gateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        stateImage = (ImageView) findViewById(R.id.stateImage);
        stateText = (TextView) findViewById(R.id.stateText);
        gateButton = (FloatingActionButton) findViewById(R.id.gateButton);
        registrationReceiver = new RegistrationReceiver();

        gateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AsyncTask<Void, Void, Void>() {
                    protected Void doInBackground(Void... params) {
                        try {
                            new GateService(getPreferences()).buttonDown();
                        } catch (IOException e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        return null;
                    }
                }.execute();
            }
        });

        SharedPreferences prefs = getPreferences();
        if(!prefs.contains(Constants.PREF_URL) || prefs.getString(Constants.PREF_URL, "").isEmpty()) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else {
            registerGateStateListener();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(registrationReceiver, new IntentFilter(Constants.REGISTRATION_COMPLETE));
        getPreferences().registerOnSharedPreferenceChangeListener(this);
        updateState();

        //try to register again if we return
        if(gateButton.getVisibility() == Button.INVISIBLE) {
            registerGateStateListener();
        }
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(registrationReceiver);
        getPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    private SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    private void registerGateStateListener() {
        /*
         * Check the device to make sure it has the Google Play Services APK. If
         * it doesn't, display a dialog that allows users to download the APK from
         * the Google Play Store or enable it in the device's system settings.
         */
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }
        } else {
            startService(new Intent(this, GCMRegistration.class));
        }
    }

    private void updateState() {
        String stateValue = getPreferences().getString(Constants.GATE_STATE, null);
        GateState state = GateState.fromValue(stateValue);

        if (state == GateState.CLOSED) {
            stateImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_lock_outline));
            stateText.setText(R.string.gate_switch_closed);
        } else if (state == GateState.OPEN) {
            stateImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_lock_open));
            stateText.setText(R.string.gate_switch_open);
        } else {
            Toast.makeText(getApplicationContext(), R.string.unknown_state_error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(Constants.GATE_STATE.equals(key)) {
            this.updateState();
        }
    }

    private class RegistrationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Exception e = (Exception) intent.getSerializableExtra(Constants.REGISTRATION_ERROR);

            if (e != null) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            } else {
                registrationProgressBar.setVisibility(ProgressBar.GONE);
                gateButton.setVisibility(FloatingActionButton.VISIBLE);
                stateImage.setVisibility(ImageView.VISIBLE);
                stateText.setVisibility(TextView.VISIBLE);
            }
        }
    }
}
