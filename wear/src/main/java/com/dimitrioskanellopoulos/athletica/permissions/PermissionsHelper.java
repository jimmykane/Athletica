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
    /**
     * Whether we are on Marshmallow and permissions checks are needed
     */
    private final static Boolean requiresRuntimePermissions = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;

    private final Context context;

    public static final String PERMISSIONS_CHANGED_BROADCAST = "PERMISSIONS_CHANGED_BROADCAST";
    public static final String PERMISSION_CHECK = "PERMISSION_CHECK";
    public static final String PERMISSION_MISSING = "PERMISSION_MISSING";
    public static final String PERMISSION_GRANTED = "PERMISSION_GRANTED";
    public static final String PERMISSION_DENIED = "PERMISSION_DENIED";
    public static final String PERMISSION_DENIED_DO_NOT_ASK_AGAIN = "PERMISSION_DENIED_DO_NOT_ASK_AGAIN";

    private final LinkedHashMap<String, String> permissions = new LinkedHashMap<>();

    /**
     * Broadcast receiver for when a the permissions request has granted permissions
     */
    private BroadcastReceiver permissionsChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getExtras().get("status").toString();
            String permission = intent.getExtras().get("permission").toString();
            switch (status){
                case PERMISSION_GRANTED:
                    Toast.makeText(context, context.getResources().getText(R.string.permissions_rationale) + "[" + permission + "]", Toast.LENGTH_SHORT).show();
                    break;
                case PERMISSION_DENIED:
                    Toast.makeText(context, context.getResources().getText(R.string.permissions_rationale) + "[" + permission + "]", Toast.LENGTH_SHORT).show();
                    break;
                case PERMISSION_DENIED_DO_NOT_ASK_AGAIN:
                    Toast.makeText(context, context.getResources().getText(R.string.permissions_do_not_ask_again_message) + " [" + permission + "]", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            permissions.put(permission, status);
        }
    };

    public PermissionsHelper(Context context, String[] permissions) {
        this.context = context;
        for (String permission : permissions){
            this.permissions.put(permission, PERMISSION_CHECK);
        }
        registerPermissionsChangedReceiver();
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
        return !Objects.equals(permissions.get(permission), PERMISSION_DENIED_DO_NOT_ASK_AGAIN);
    }

    /**
     * Another wrapper for firing an intent
     */
    public void askForPermission(String permission) {
        Intent permissionIntent = new Intent(
                context,
                PermissionActivity.class);
        permissionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        permissionIntent.putExtra("permission", permission);
        context.startActivity(permissionIntent);
    }

    private void registerPermissionsChangedReceiver() {
        context.registerReceiver(permissionsChangedReceiver, new IntentFilter(PERMISSIONS_CHANGED_BROADCAST));
    }

    private void unregisterPermissionsChangedReceiver() {
        context.unregisterReceiver(permissionsChangedReceiver);
    }
}
