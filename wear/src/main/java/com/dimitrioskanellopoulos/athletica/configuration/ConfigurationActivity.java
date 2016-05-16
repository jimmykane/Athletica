package com.dimitrioskanellopoulos.athletica.configuration;

import android.hardware.Sensor;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.dimitrioskanellopoulos.athletica.R;
import com.dimitrioskanellopoulos.athletica.activities.AmbientAwareWearableActivity;
import com.dimitrioskanellopoulos.athletica.helpers.SensorHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;


// @todo add antialias support
public class ConfigurationActivity extends AmbientAwareWearableActivity {
    private final static String TAG = "ConfigurationActivity";

    private Switch switchTimeFormat;
    private Switch switchDateNames;
    private Switch switchInterlace;
    private Switch switchInvertBlackAndWhite;

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

        switchDateNames = (Switch) findViewById(R.id.switch_date_names);

        switchInterlace = (Switch) findViewById(R.id.switch_interlace);

        switchInvertBlackAndWhite = (Switch) findViewById(R.id.switch_invert_black_and_white);

        switchTimeFormat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                updateConfigDataItemTimeFormat(isChecked);
            }
        });

        switchDateNames.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                updateConfigDataItemDateNames(isChecked);
            }
        });

        switchInterlace.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                updateConfigDataItemInterlace(isChecked);
            }
        });

        switchInvertBlackAndWhite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                updateConfigDataItemInvertBlackAndWhite(isChecked);
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
        Log.d(TAG, "onStop");
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public LinearLayout getLayout() {
        return (LinearLayout) findViewById(R.id.configuration_layout);
    }

    private void createSwitchesForSensors(ArrayList<Integer> sensors){
        Switch sensorSwitch = new Switch(this);
        for (Integer applicationDeviceSupportedSensor: sensors){
            switch (applicationDeviceSupportedSensor) {
                case Sensor.TYPE_PRESSURE:
                    sensorSwitch.setText(R.string.configuration_activity_android_sensor_pressure);
                    // @todo add extra
                    break;
                case Sensor.TYPE_HEART_RATE:
                    sensorSwitch.setText(R.string.configuration_activity_android_sensor_heart_rate);
                    break;
                case Sensor.TYPE_AMBIENT_TEMPERATURE:
                    sensorSwitch.setText(R.string.configuration_activity_android_sensor_ambient_temperature);
                    break;
                case Sensor.TYPE_LIGHT:
                    sensorSwitch.setText(R.string.configuration_activity_android_sensor_light);
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    sensorSwitch.setText(R.string.configuration_activity_android_sensor_magnetic_field);
                    break;
                default:
                    continue;
            }
            sensorSwitch.setId(applicationDeviceSupportedSensor);
            sensorSwitch.setChecked(false);
            getLayout().addView(sensorSwitch);
        }
    }


    private void updateConfigDataItemTimeFormat(boolean format24) {
        DataMap configKeysToOverwrite = new DataMap();
        configKeysToOverwrite.putBoolean(ConfigurationHelper.KEY_TIME_FORMAT,
                format24);
        ConfigurationHelper.overwriteKeysInConfigDataMap(googleApiClient, configKeysToOverwrite);
    }

    private void updateConfigDataItemDateNames(boolean date_names) {
        DataMap configKeysToOverwrite = new DataMap();
        configKeysToOverwrite.putBoolean(ConfigurationHelper.KEY_DATE_NAMES,
                date_names);
        ConfigurationHelper.overwriteKeysInConfigDataMap(googleApiClient, configKeysToOverwrite);
    }

    private void updateConfigDataItemInterlace(boolean interlace) {
        DataMap configKeysToOverwrite = new DataMap();
        configKeysToOverwrite.putBoolean(ConfigurationHelper.KEY_INTERLACE,
                interlace);
        ConfigurationHelper.overwriteKeysInConfigDataMap(googleApiClient, configKeysToOverwrite);
    }

    private void updateConfigDataItemInvertBlackAndWhite(boolean invertBlackAndWhite) {
        DataMap configKeysToOverwrite = new DataMap();
        configKeysToOverwrite.putBoolean(ConfigurationHelper.KEY_INVERT_BLACK_AND_WHITE,
                invertBlackAndWhite);
        ConfigurationHelper.overwriteKeysInConfigDataMap(googleApiClient, configKeysToOverwrite);
    }

    private void updateConfigDataOnStartup() {
        ConfigurationHelper.fetchConfigDataMap(googleApiClient,
                new ConfigurationHelper.FetchConfigDataMapCallback() {
                    @Override
                    public void onConfigDataMapFetched(DataMap startupConfig) {
                        // If the DataItem hasn't been created yet or some keys are missing,
                        // use the default values.
                        ConfigurationHelper.setDefaultValuesForMissingConfigKeys(getApplicationContext(), startupConfig);
                        ConfigurationHelper.putConfigDataItem(googleApiClient, startupConfig);
                        updateUiForConfigDataMap(startupConfig);
                    }
                }
        );
    }

    private void updateUiForConfigDataMap(final DataMap config) {
        for (String key : config.keySet()) {
            if (!config.containsKey(key)) {
                Log.w(TAG, "No value found for config key:" + key);
                continue;
            }
            switch (key) {
                case ConfigurationHelper.KEY_TIME_FORMAT:
                    switchTimeFormat.setChecked(config.getBoolean(key));
                    break;
                case ConfigurationHelper.KEY_DATE_NAMES:
                    switchDateNames.setChecked(config.getBoolean(key));
                    break;
                case ConfigurationHelper.KEY_INTERLACE:
                    switchInterlace.setChecked(config.getBoolean(key));
                    break;
                case ConfigurationHelper.KEY_INVERT_BLACK_AND_WHITE:
                    switchInvertBlackAndWhite.setChecked(config.getBoolean(key));
                    break;
                case ConfigurationHelper.KEY_SENSORS:
                    ArrayList<Integer> sensors = config.getIntegerArrayList(key);
                    createSwitchesForSensors(sensors);
                default:
                    Log.w(TAG, "Ignoring unknown config key: " + key);
                    break;
            }
        }
    }
}
