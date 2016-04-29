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

public class PermissionsHelper {
    /**
     * Whether we are on Marshmallow and permissions checks are needed
     */
    private final static Boolean requiresRuntimePermissions = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;

    private final Context context;

    public static final String PERMISSIONS_CHANGED_BROADCAST = "PERMISSIONS_CHANGED_BROADCAST";
    public static final String PERMISSIONS_GRANTED = "PERMISSIONS_GRANTED";
    public static final String PERMISSIONS_DENIED = "PERMISSIONS_DENIED";

    /**
     * Broadcast receiver for when a the permissions request has granted permissions
     */
    private BroadcastReceiver permissionsChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getExtras().get("status").toString();
            String permission = intent.getExtras().get("permission").toString();
            switch (status){
                case PERMISSIONS_GRANTED:
                    Toast.makeText(context, "Enabled permission: " + permission, Toast.LENGTH_SHORT).show();
                    break;
                case PERMISSIONS_DENIED:
                    Toast.makeText(context, context.getResources().getText(R.string.permissions_rationale) + "[" + permission + "]", Toast.LENGTH_SHORT).show();
                default:
                    break;
            }
        }
    };

    public PermissionsHelper(Context context) {
        this.context = context;
        registerPermissionsGrantedReceiver();
    }

    /**
     * Just a wrapper to help
     */
    @NonNull
    public Boolean hasPermission(String permission) {
        return !requiresRuntimePermissions || (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * Another wrapper for firing an intent
     */
    public Intent getIntentForPermission(String permission) {
        Intent permissionIntent = new Intent(
                context,
                PermissionActivity.class);
        permissionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        permissionIntent.putExtra("permission", permission);
        return permissionIntent;
    }

    private void registerPermissionsGrantedReceiver() {
        context.registerReceiver(permissionsChangedReceiver, new IntentFilter(PERMISSIONS_CHANGED_BROADCAST));
    }

    private void unregisterPermissionsGrantedReceiver() {
        context.unregisterReceiver(permissionsChangedReceiver);
    }
}
