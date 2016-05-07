package com.dimitrioskanellopoulos.athletica.configuration;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.dimitrioskanellopoulos.athletica.helpers.EmulatorHelper;
import com.dimitrioskanellopoulos.athletica.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Wearable;


// @todo add antialias support
public class ConfigurationActivity extends WearableActivity {
    private final static String TAG = "ConfigurationActivity";

    private GoogleApiClient googleApiClient;


    private Switch switchTimeFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration);
        if (EmulatorHelper.isEmulator()){
            setContentView(R.layout.configuration_dev);

        }
        setAmbientEnabled();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();

        switchTimeFormat = (Switch) findViewById(R.id.switch_24_hour_clock);

        switchTimeFormat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    Toast.makeText(getApplicationContext(), "Time will set in 24h format", Toast.LENGTH_SHORT).show();
                    updateConfigDataItemTimeFormat(true);
                }else{
                    Toast.makeText(getApplicationContext(), "Time will set 12h format", Toast.LENGTH_SHORT).show();
                    updateConfigDataItemTimeFormat(false);
                }

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    public void onClickAtLayout(View view) {
        finish();
    }

    private void updateConfigDataItemTimeFormat(boolean format24) {
        DataMap configKeysToOverwrite = new DataMap();
        configKeysToOverwrite.putBoolean(ConfigurationHelper.KEY_TIME_FORMAT,
                format24);
        ConfigurationHelper.overwriteKeysInConfigDataMap(googleApiClient, configKeysToOverwrite);
    }
}
