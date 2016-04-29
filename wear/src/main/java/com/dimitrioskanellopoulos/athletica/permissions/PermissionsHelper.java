package com.dimitrioskanellopoulos.athletica.permissions;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.dimitrioskanellopoulos.athletica.R;

import java.util.LinkedHashMap;
import java.util.Objects;

public class PermissionsHelper {
    private final static String TAG = "PermissionsHelper";
    /**
     * Whether we are on Marshmallow and permissions checks are needed
     */
    private final static Boolean requiresRuntimePermissions = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;

    private final Context context;

    public static final String PERMISSIONS_CHANGED_BROADCAST = "PERMISSIONS_CHANGED_BROADCAST";

    public static final String PERMISSIONS_ACTIVITY_INTENT_FINISHED = "PERMISSIONS_ACTIVITY_INTENT_FINISHED";

    public static final String PERMISSION_STATUS_UNKNOWN = "PERMISSION_STATUS_UNKNOWN";
    public static final String PERMISSION_STATUS_GRANTED = "PERMISSION_STATUS_GRANTED";
    public static final String PERMISSION_STATUS_DENIED = "PERMISSION_STATUS_DENIED";
    public static final String PERMISSION_STATUS_DENIED_DO_NOT_ASK_AGAIN = "PERMISSION_STATUS_DENIED_DO_NOT_ASK_AGAIN";

    private final LinkedHashMap<String, String> permissions = new LinkedHashMap<>();

    private Boolean isPermissionActivityIntentStarted = false;

    /**
     * Broadcast receiver changes in the permissions
     */
    private BroadcastReceiver permissionsChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getExtras().getString("status");
            String permission = intent.getExtras().getString("permission");
            switch (status){
                case PERMISSION_STATUS_GRANTED:
                    Toast.makeText(context, context.getResources().getText(R.string.permissions_rationale) + "[" + permission + "]", Toast.LENGTH_SHORT).show();
                    break;
                case PERMISSION_STATUS_DENIED:
                    Toast.makeText(context, context.getResources().getText(R.string.permissions_rationale) + "[" + permission + "]", Toast.LENGTH_SHORT).show();
                    break;
                case PERMISSION_STATUS_DENIED_DO_NOT_ASK_AGAIN:
                    Toast.makeText(context, context.getResources().getText(R.string.permissions_do_not_ask_again_message) + " [" + permission + "]", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            permissions.put(permission, status);
        }
    };


    private BroadcastReceiver permissionsActivityIntentFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isPermissionActivityIntentStarted = false;
        }
    };


    public PermissionsHelper(Context context, String[] permissions) {
        this.context = context;
        for (String permission : permissions){
            this.permissions.put(permission, PERMISSION_STATUS_UNKNOWN);
        }
        registerPermissionsChangedReceiver();
        registerPermissionActivityIntentFinishedReceiver();
    }

    /**
     * Just a wrapper to help
     */
    @NonNull
    public Boolean hasPermission(String permission) {
        return !requiresRuntimePermissions || (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);
    }

    @NonNull
    public Boolean canAskAgainForPermission(String permission) {
        return !Objects.equals(permissions.get(permission), PERMISSION_STATUS_DENIED_DO_NOT_ASK_AGAIN);
    }

    /**
     * Another wrapper for firing an intent
     */
    public void askForPermission(String permission) {
        if (isPermissionActivityIntentStarted){
            return;
        }
        Intent permissionIntent = new Intent(
                context,
                PermissionActivity.class);
        permissionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        permissionIntent.putExtra("permission", permission);
        context.startActivity(permissionIntent);
        isPermissionActivityIntentStarted = true;
    }

    private void registerPermissionsChangedReceiver() {
        context.registerReceiver(permissionsChangedReceiver, new IntentFilter(PERMISSIONS_CHANGED_BROADCAST));
    }

    private void unregisterPermissionsChangedReceiver() {
        context.unregisterReceiver(permissionsChangedReceiver);
    }

    private void registerPermissionActivityIntentFinishedReceiver() {
        context.registerReceiver(permissionsActivityIntentFinishedReceiver, new IntentFilter(PERMISSIONS_ACTIVITY_INTENT_FINISHED));
    }

    private void unregisterPermissionActivityIntentFinishedReceiver() {
        context.unregisterReceiver(permissionsActivityIntentFinishedReceiver);
    }
}
