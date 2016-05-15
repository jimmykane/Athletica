package com.dimitrioskanellopoulos.athletica.configuration;

import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.dimitrioskanellopoulos.athletica.R;
import com.dimitrioskanellopoulos.athletica.activities.AmbientAwareWearableActivity;
import com.dimitrioskanellopoulos.athletica.helpers.SensorHelper;
import com.dimitrioskanellopoulos.athletica.sensors.CallbackSensor;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Wearable;


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

        /**
         * Add the switches for the sensors
         */
        addSwitchesForSensors();
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
    public LinearLayout getLayout() {
        return (LinearLayout) findViewById(R.id.configuration_layout);
    }

    private void addSwitchesForSensors(){
        Switch sensorSwitch = new Switch(this);
        for (Integer applicationDeviceSupportedSensor: SensorHelper.getApplicationDeviceSupportedSensors(getApplicationContext())){
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
        switch (configKey) {
            case ConfigurationHelper.KEY_TIME_FORMAT:
                switchTimeFormat.setChecked(value);
                break;
            case ConfigurationHelper.KEY_DATE_NAMES:
                switchDateNames.setChecked(value);
                break;
            case ConfigurationHelper.KEY_INTERLACE:
                switchInterlace.setChecked(value);
                break;
            case ConfigurationHelper.KEY_INVERT_BLACK_AND_WHITE:
                switchInvertBlackAndWhite.setChecked(value);
                break;
            default:
                Log.w(TAG, "Ignoring unknown config key: " + configKey);
                return false;
        }
        return true;
    }
}
