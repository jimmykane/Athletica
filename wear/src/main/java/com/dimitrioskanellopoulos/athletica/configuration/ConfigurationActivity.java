package com.dimitrioskanellopoulos.athletica.configuration;

import android.Manifest;
import android.hardware.Sensor;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Arrays;

public class ConfigurationActivity extends AmbientAwareWearableActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final static String TAG = "ConfigurationActivity";

    private Switch switchTimeFormat;
    private Switch switchDateNames;
    private Switch switchShowSunriseSunset;
    private Switch switchInterlace;
    private Switch switchAntialiasInAmbientMode;
    private Switch switchInvertBlackAndWhite;
    private Switch switchDayNightMode;
    private Switch switchTwoColorBackground;
    private Switch switchShowGoogleFitSteps;
    private Switch switchShowUnreadNotificationCount;
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
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();

        switchTimeFormat = (Switch) findViewById(R.id.switch_24_hour_clock);

        switchDateNames = (Switch) findViewById(R.id.switch_date_names);

        switchShowSunriseSunset = (Switch) findViewById(R.id.switch_show_sunrise_sunset);

        switchInterlace = (Switch) findViewById(R.id.switch_interlace);

        switchAntialiasInAmbientMode = (Switch) findViewById(R.id.switch_antialias_in_ambient_mode);

        switchInvertBlackAndWhite = (Switch) findViewById(R.id.switch_invert_black_and_white);

        switchDayNightMode = (Switch) findViewById(R.id.switch_day_night_mode);

        switchTwoColorBackground = (Switch) findViewById(R.id.switch_two_color_background);

        switchShowGoogleFitSteps = (Switch) findViewById(R.id.switch_show_google_fit_steps);

        switchShowUnreadNotificationCount = (Switch) findViewById(R.id.switch_show_unread_notification_count);

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

        switchShowSunriseSunset.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                updateConfigDataItemShowSunriseSunsetTimes(isChecked);
            }
        });

        switchInterlace.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                updateConfigDataItemInterlace(isChecked);
            }
        });

        switchAntialiasInAmbientMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                updateConfigDataItemAntialiasInAmbient(isChecked);
            }
        });

        switchInvertBlackAndWhite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                updateConfigDataItemInvertBlackAndWhite(isChecked);
            }
        });

        switchDayNightMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                updateConfigDataItemDayNightMode(isChecked);
            }
        });

        switchTwoColorBackground.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                updateConfigDataItemTwoColorBackground(isChecked);
            }
        });

        switchShowGoogleFitSteps.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                updateConfigDataItemShowGoogleFitSteps(isChecked);
            }
        });

        switchShowUnreadNotificationCount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                updateConfigDataItemShowUnreadNotificationCount(isChecked);
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

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected: " + connectionHint);
        updateConfigDataOnStartup();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "onConnectionSuspended: " + cause);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: " + connectionResult);
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
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                sensorSwitch.setText(R.string.configuration_activity_android_sensor_relative_humidity);
                break;
            case Sensor.TYPE_ACCELEROMETER:
                sensorSwitch.setText(R.string.configuration_activity_android_sensor_accelerometer);
                break;
            default:
                return;
        }
        sensorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // 0. Try to set it on
                Switch sensorSwitch = (Switch) findViewById(sensorType);

                setSwitchChecked(sensorSwitch, isChecked);

                /**
                 * When a sensors state changes
                 * 1. Loop over the switches
                 * 2. Create list with the ones that are on
                 * 3. Save them as the enabled sensors
                 */
                ArrayList<Integer> enabledSensors = new ArrayList<>();
                for (Integer sensor : sensors) {
                    Switch enabledSensorSwitch = (Switch) findViewById(sensor);
                    if (enabledSensorSwitch != null && enabledSensorSwitch.isChecked()) {
                        enabledSensors.add(sensor);
                    }
                }
                updateConfigDataItemSensors(enabledSensors);
            }
        });
        getLayout().addView(sensorSwitch);
    }

    private void setSwitchChecked(Switch aSwitch, Boolean checked) {
        switch (aSwitch.getId()) {
            case Sensor.TYPE_HEART_RATE:
                if (checked && !permissionsHelper.hasPermission(Manifest.permission.BODY_SENSORS)) {
                    checked = false;
                    if (permissionsHelper.canAskAgainForPermission(Manifest.permission.BODY_SENSORS)) {
                        permissionsHelper.askForPermission(Manifest.permission.BODY_SENSORS);
                    }
                }
                aSwitch.setChecked(checked);
                Log.d(TAG, "Set checked to: " + checked.toString() + " for " + aSwitch.getText());
                break;
            default:
                aSwitch.setChecked(checked);
                Log.d(TAG, "Set checked to: " + checked.toString() + " for " + aSwitch.getText());
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

    private void updateConfigDataItemShowSunriseSunsetTimes(boolean showSunriseSunset) {
        DataMap configMap = new DataMap();
        configMap.putBoolean(ConfigurationHelper.KEY_SHOW_SUNRISE_SUNSET,
                showSunriseSunset);
        ConfigurationHelper.overwriteKeysInConfigDataMap(googleApiClient, configMap);
    }

    private void updateConfigDataItemInterlace(boolean interlace) {
        DataMap configMap = new DataMap();
        configMap.putBoolean(ConfigurationHelper.KEY_INTERLACE,
                interlace);
        ConfigurationHelper.overwriteKeysInConfigDataMap(googleApiClient, configMap);
    }

    private void updateConfigDataItemAntialiasInAmbient(boolean antialiasInAmbient) {
        DataMap configMap = new DataMap();
        configMap.putBoolean(ConfigurationHelper.KEY_ANTIALIAS_IN_AMBIENT_MODE,
                antialiasInAmbient);
        ConfigurationHelper.overwriteKeysInConfigDataMap(googleApiClient, configMap);
    }

    private void updateConfigDataItemInvertBlackAndWhite(boolean invertBlackAndWhite) {
        DataMap configMap = new DataMap();
        configMap.putBoolean(ConfigurationHelper.KEY_INVERT_BLACK_AND_WHITE,
                invertBlackAndWhite);
        ConfigurationHelper.overwriteKeysInConfigDataMap(googleApiClient, configMap);
    }

    private void updateConfigDataItemDayNightMode(boolean dayNightMode) {
        DataMap configMap = new DataMap();
        configMap.putBoolean(ConfigurationHelper.KEY_DAY_NIGHT_MODE,
                dayNightMode);
        ConfigurationHelper.overwriteKeysInConfigDataMap(googleApiClient, configMap);
    }

    private void updateConfigDataItemTwoColorBackground(boolean twoColorBackground) {
        DataMap configMap = new DataMap();
        configMap.putBoolean(ConfigurationHelper.KEY_TWO_COLOR_BACKGROUND,
                twoColorBackground);
        ConfigurationHelper.overwriteKeysInConfigDataMap(googleApiClient, configMap);
    }

    private void updateConfigDataItemShowGoogleFitSteps(boolean showGoogleFitSteps) {
        DataMap configMap = new DataMap();
        configMap.putBoolean(ConfigurationHelper.KEY_SHOW_GOOGLE_FIT_STEPS,
                showGoogleFitSteps);
        ConfigurationHelper.overwriteKeysInConfigDataMap(googleApiClient, configMap);
    }

    private void updateConfigDataItemShowUnreadNotificationCount(boolean showUnreadNotificationCount) {
        DataMap configMap = new DataMap();
        configMap.putBoolean(ConfigurationHelper.KEY_SHOW_UNREAD_NOTIFICATION_COUNT,
                showUnreadNotificationCount);
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
                case ConfigurationHelper.KEY_SHOW_SUNRISE_SUNSET:
                    switchShowSunriseSunset.setChecked(config.getBoolean(key));
                    break;
                case ConfigurationHelper.KEY_INTERLACE:
                    switchInterlace.setChecked(config.getBoolean(key));
                    break;
                case ConfigurationHelper.KEY_ANTIALIAS_IN_AMBIENT_MODE:
                    switchAntialiasInAmbientMode.setChecked(config.getBoolean(key));
                    break;
                case ConfigurationHelper.KEY_INVERT_BLACK_AND_WHITE:
                    switchInvertBlackAndWhite.setChecked(config.getBoolean(key));
                    break;
                case ConfigurationHelper.KEY_DAY_NIGHT_MODE:
                    switchDayNightMode.setChecked(config.getBoolean(key));
                    break;
                case ConfigurationHelper.KEY_TWO_COLOR_BACKGROUND:
                    switchTwoColorBackground.setChecked(config.getBoolean(key));
                    break;
                case ConfigurationHelper.KEY_SHOW_GOOGLE_FIT_STEPS:
                    switchShowGoogleFitSteps.setChecked(config.getBoolean(key));
                    break;
                case ConfigurationHelper.KEY_SHOW_UNREAD_NOTIFICATION_COUNT:
                    switchShowUnreadNotificationCount.setChecked(config.getBoolean(key));
                    break;
                case ConfigurationHelper.KEY_ENABLED_SENSORS:
                    ArrayList<Integer> enabledSensors = config.getIntegerArrayList(key);
                    Log.d(TAG, "Config enabled sensors: " + enabledSensors.toString() + " Application/Device sensors: " + sensors.toString());
                    for (Integer sensor : sensors) {
                        Switch sensorSwitch = (Switch) findViewById(sensor);
                        if (enabledSensors.contains(sensor)) {
                            setSwitchChecked(sensorSwitch, true);
                        } else {
                            setSwitchChecked(sensorSwitch, false);
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
