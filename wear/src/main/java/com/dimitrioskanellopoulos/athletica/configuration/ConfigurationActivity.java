package com.dimitrioskanellopoulos.athletica.configuration;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dimitrioskanellopoulos.athletica.activities.AmbientAwareWearableActivity;
import com.dimitrioskanellopoulos.athletica.helpers.EmulatorHelper;
import com.dimitrioskanellopoulos.athletica.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Wearable;


// @todo add antialias support
public class ConfigurationActivity extends AmbientAwareWearableActivity {
    private final static String TAG = "ConfigurationActivity";

    private Switch switchTimeFormat;
    private Switch switchDayName;
    private Switch switchInterlace;

    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration);
        setAmbientEnabled();

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                        updateConfigDataOnStartup();
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

        switchDayName = (Switch) findViewById(R.id.switch_day_name);

        switchInterlace = (Switch) findViewById(R.id.switch_interlace);

        switchTimeFormat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    updateConfigDataItemTimeFormat(true);
                } else {
                    updateConfigDataItemTimeFormat(false);
                }
            }
        });

        switchDayName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    updateConfigDataItemDayName(true);
                } else {
                    updateConfigDataItemDayName(false);
                }
            }
        });

        switchInterlace.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    updateConfigDataItemInterlace(true);
                } else {
                    updateConfigDataItemInterlace(false);
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

    @Override
    public LinearLayout getLayout(){
        return (LinearLayout) findViewById(R.id.configuration_layout);
    }

    private void updateConfigDataItemTimeFormat(boolean format24) {
        DataMap configKeysToOverwrite = new DataMap();
        configKeysToOverwrite.putBoolean(ConfigurationHelper.KEY_TIME_FORMAT,
                format24);
        ConfigurationHelper.overwriteKeysInConfigDataMap(googleApiClient, configKeysToOverwrite);
    }

    private void updateConfigDataItemDayName(boolean interlace) {
        DataMap configKeysToOverwrite = new DataMap();
        configKeysToOverwrite.putBoolean(ConfigurationHelper.KEY_DAY_NAME,
                interlace);
        ConfigurationHelper.overwriteKeysInConfigDataMap(googleApiClient, configKeysToOverwrite);
    }

    private void updateConfigDataItemInterlace(boolean interlace) {
        DataMap configKeysToOverwrite = new DataMap();
        configKeysToOverwrite.putBoolean(ConfigurationHelper.KEY_INTERLACE,
                interlace);
        ConfigurationHelper.overwriteKeysInConfigDataMap(googleApiClient, configKeysToOverwrite);
    }

    private void updateConfigDataOnStartup() {
        ConfigurationHelper.fetchConfigDataMap(googleApiClient,
                new ConfigurationHelper.FetchConfigDataMapCallback() {
                    @Override
                    public void onConfigDataMapFetched(DataMap startupConfig) {
                        // If the DataItem hasn't been created yet or some keys are missing,
                        // use the default values.
                        ConfigurationHelper.setDefaultValuesForMissingConfigKeys(startupConfig);
                        ConfigurationHelper.putConfigDataItem(googleApiClient, startupConfig);
                        updateUiForConfigDataMap(startupConfig);
                    }
                }
        );
    }

    private void updateUiForConfigDataMap(final DataMap config) {
        for (String configKey : config.keySet()) {
            if (!config.containsKey(configKey)) {
                continue;
            }
            Boolean value = config.getBoolean(configKey);
            if (Log.isLoggable(TAG, Log.DEBUG)) {
                Log.d(TAG, "Found watch face config key: " + configKey + " -> "
                        + Boolean.toString(value));
            }
            updateUiForKey(configKey, value);
        }
    }

    private boolean updateUiForKey(String configKey, Boolean value) {
        if (configKey.equals(ConfigurationHelper.KEY_TIME_FORMAT)) {
            switchTimeFormat.setChecked(value);
        }if(configKey.equals(ConfigurationHelper.KEY_DAY_NAME)){
            switchDayName.setChecked(value);
        } else if(configKey.equals(ConfigurationHelper.KEY_INTERLACE)){
            switchInterlace.setChecked(value);
        }else {
            Log.w(TAG, "Ignoring unknown config key: " + configKey);
            return false;
        }
        return true;
    }
}
