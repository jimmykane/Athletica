package com.dimitrioskanellopoulos.athletica.configuration;

import android.Manifest;
import android.hardware.Sensor;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.dimitrioskanellopoulos.athletica.R;
import com.dimitrioskanellopoulos.athletica.activities.AmbientAwareWearableActivity;
import com.dimitrioskanellopoulos.athletica.helpers.SensorHelper;
import com.dimitrioskanellopoulos.athletica.permissions.PermissionsHelper;
import com.dimitrioskanellopoulos.athletica.sensors.CallbackSensor;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Arrays;

public class ConfigurationActivity extends AmbientAwareWearableActivity implements DataApi.DataListener{
    private final static String TAG = "ConfigurationActivity";

    private Switch switchTimeFormat;
    private Switch switchDateNames;
    private Switch switchInterlace;
    private Switch switchInvertBlackAndWhite;
    private ArrayList<Integer> sensors;

    private PermissionsHelper permissionsHelper;

    private GoogleApiClient googleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionsHelper = new PermissionsHelper(getApplicationContext(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BODY_SENSORS});
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

        // Get the available sensors
        sensors = SensorHelper.getApplicationDeviceSupportedSensors(getApplicationContext());
        createSwitchesForSensorTypes(sensors);
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

    @Override // DataApi.DataListener
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "Data changed");
        for (DataEvent dataEvent : dataEvents) {
            if (dataEvent.getType() != DataEvent.TYPE_CHANGED) {
                continue;
            }

            DataItem dataItem = dataEvent.getDataItem();
            if (!dataItem.getUri().getPath().equals(
                    ConfigurationHelper.PATH_WITH_FEATURE)) {
                continue;
            }

            DataMapItem dataMapItem = DataMapItem.fromDataItem(dataItem);
            DataMap config = dataMapItem.getDataMap();
            Log.d(TAG, "Config DataItem updated " + config);
            // This can happen from this method more often when phone changes
            updateUiForConfigDataMap(config);
        }
    }

    private void createSwitchesForSensorTypes(ArrayList<Integer> sensorTypes) {
        for (Integer sensorType : sensorTypes) {
            createSwitchesForSensorType(sensorType, false);
        }
    }

    private void createSwitchesForSensorType(final Integer sensorType, Boolean checked) {
        Switch sensorSwitch = new Switch(this);
        sensorSwitch.setId(sensorType);
        sensorSwitch.setChecked(checked);
        switch (sensorType) {
            case Sensor.TYPE_PRESSURE:
                sensorSwitch.setText(R.string.configuration_activity_android_sensor_pressure);
                break;
            case CallbackSensor.TYPE_PRESSURE_ALTITUDE:
                sensorSwitch.setText(R.string.configuration_activity_android_sensor_pressure_altitude);
                break;
            case Sensor.TYPE_HEART_RATE:
                sensorSwitch.setText(R.string.configuration_activity_android_sensor_heart_rate);
                if (!permissionsHelper.hasPermission(Manifest.permission.BODY_SENSORS)) {
                    sensorSwitch.setChecked(false);
                }
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
                return;
        }
        sensorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // 0. Try to set it on
                setSensorSwitchChecked(sensorType, isChecked);

                /**
                 * When a sensors state changes
                 * 1. Loop over the switches
                 * 2. Create list with the ones that are on
                 * 3. Save them as the enabled sensors
                 */
                ArrayList<Integer> enabledSensors = new ArrayList<>();
                for (Integer sensor : sensors) {
                    Switch sensorSwitch = (Switch) findViewById(sensor);
                    if (sensorSwitch.isChecked()) {
                        enabledSensors.add(sensor);
                    }
                }
                updateConfigDataItemSensors(enabledSensors);
            }
        });
        getLayout().addView(sensorSwitch);
    }

    private void setSensorSwitchChecked(Integer sensorType, Boolean checked) {
        Switch sensorSwitch = (Switch) findViewById(sensorType);
        if (sensorSwitch == null) {
            Log.w(TAG, "No switch found for sensor type " + sensorType);
            return;
        }
        switch (sensorType) {
            case Sensor.TYPE_HEART_RATE:
                if (checked && !permissionsHelper.hasPermission(Manifest.permission.BODY_SENSORS)) {
                    checked = false;
                    if (permissionsHelper.canAskAgainForPermission(Manifest.permission.BODY_SENSORS)) {
                        permissionsHelper.askForPermission(Manifest.permission.BODY_SENSORS);
                    }
                }
                sensorSwitch.setChecked(checked);
                Log.d(TAG, "Set checked to: " + checked.toString() + " for " + sensorType);
                break;
            default:
                sensorSwitch.setChecked(checked);
                Log.d(TAG, "Set checked to: " + checked.toString() + " for " + sensorType);
                break;
        }
    }

    private void updateConfigDataItemTimeFormat(boolean format24) {
        DataMap configMap = new DataMap();
        configMap.putBoolean(ConfigurationHelper.KEY_TIME_FORMAT,
                format24);
        ConfigurationHelper.overwriteKeysInConfigDataMap(googleApiClient, configMap);
    }

    private void updateConfigDataItemDateNames(boolean date_names) {
        DataMap configMap = new DataMap();
        configMap.putBoolean(ConfigurationHelper.KEY_DATE_NAMES,
                date_names);
        ConfigurationHelper.overwriteKeysInConfigDataMap(googleApiClient, configMap);
    }

    private void updateConfigDataItemInterlace(boolean interlace) {
        DataMap configMap = new DataMap();
        configMap.putBoolean(ConfigurationHelper.KEY_INTERLACE,
                interlace);
        ConfigurationHelper.overwriteKeysInConfigDataMap(googleApiClient, configMap);
    }

    private void updateConfigDataItemInvertBlackAndWhite(boolean invertBlackAndWhite) {
        DataMap configMap = new DataMap();
        configMap.putBoolean(ConfigurationHelper.KEY_INVERT_BLACK_AND_WHITE,
                invertBlackAndWhite);
        ConfigurationHelper.overwriteKeysInConfigDataMap(googleApiClient, configMap);
    }

    private void updateConfigDataItemSensors(ArrayList<Integer> enabledSensors) {
        Log.d(TAG, "Updating config for enabled sensors " + Arrays.toString(enabledSensors.toArray()));
        DataMap configMap = new DataMap();
        configMap.putIntegerArrayList(ConfigurationHelper.KEY_ENABLED_SENSORS,
                enabledSensors);
        ConfigurationHelper.overwriteKeysInConfigDataMap(googleApiClient, configMap);
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
                case ConfigurationHelper.KEY_ENABLED_SENSORS:
                    ArrayList<Integer> enabledSensors = config.getIntegerArrayList(key);
                    Log.d(TAG, "Config enabled sensors: " + enabledSensors.toString() + " Application/Device sensors: " + sensors.toString());
                    for (Integer sensor : sensors) {
                        if (enabledSensors.contains(sensor)) {
                            setSensorSwitchChecked(sensor, true);
                        } else {
                            setSensorSwitchChecked(sensor, false);
                        }
                    }
                    break;
                default:
                    Log.w(TAG, "Ignoring unknown config key: " + key);
                    break;
            }
        }
    }
}
