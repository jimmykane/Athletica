package com.dimitrioskanellopoulos.athletica.configuration;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.dimitrioskanellopoulos.athletica.permissions.PermissionActivity;

import java.util.Objects;

public class ConfigurationHelper {
    private final static String TAG = "ConfigurationHelper";

    private final Context context;

    public static final String CONFIGURATION_CHANGED_BROADCAST = "CONFIGURATION_CHANGED_BROADCAST";

    public static final String CONFIGURATION_ACTIVITY_FINISHED = "CONFIGURATION_ACTIVITY_FINISHED";

    private Boolean isRegisteredConfigurationChangedReceiver = false;
    private Boolean isRegisteredConfigurationActivityFinishedReceiver = false;

    /**
     * Broadcast receiver changes in the configuration
     */
    private BroadcastReceiver configurationChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };


    /**
     * When the activity ends then lets us add it as state
     */
    private BroadcastReceiver configurationActivityFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            unregisterConfigurationChangedReceiver();
            unregisterPermissionActivityIntentFinishedReceiver();
        }
    };


    public ConfigurationHelper(Context context, String[] permissions) {
        this.context = context;
    }

    /**
     * Another wrapper for firing an intent
     */
    public void askForConfiguration(String permission) {
//        Intent permissionIntent = new Intent(
//                context,
//                PermissionActivity.class);
//        permissionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        permissionIntent.putExtra("permission", permission);
//        context.startActivity(permissionIntent);
//        registerConfigurationActivityFinishedReceiver();
//        registerConfigurationChangedReceiver();
    }

    private void registerConfigurationChangedReceiver() {
        Log.d(TAG, "registerConfigurationChangedReceiver");
        if (isRegisteredConfigurationChangedReceiver) {
            return;
        }
        context.registerReceiver(configurationChangedReceiver, new IntentFilter(CONFIGURATION_CHANGED_BROADCAST));
        isRegisteredConfigurationChangedReceiver = true;
    }

    private void unregisterConfigurationChangedReceiver() {
        Log.d(TAG, "unregisterConfigurationChangedReceiver");
        if (!isRegisteredConfigurationChangedReceiver){
            return;
        }
        context.unregisterReceiver(configurationChangedReceiver);
        isRegisteredConfigurationChangedReceiver = false;
    }

    private void registerConfigurationActivityFinishedReceiver() {
        Log.d(TAG, "registerConfigurationActivityFinishedReceiver");
        if (isRegisteredConfigurationActivityFinishedReceiver){
            return;
        }
        context.registerReceiver(configurationActivityFinishedReceiver, new IntentFilter(CONFIGURATION_ACTIVITY_FINISHED));
        isRegisteredConfigurationActivityFinishedReceiver = true;
    }

    private void unregisterPermissionActivityIntentFinishedReceiver() {
        Log.d(TAG, "unregisterPermissionActivityIntentFinishedReceiver");
        if (!isRegisteredConfigurationActivityFinishedReceiver){
            return;
        }
        context.unregisterReceiver(configurationActivityFinishedReceiver);
        isRegisteredConfigurationActivityFinishedReceiver = false;
    }
}
