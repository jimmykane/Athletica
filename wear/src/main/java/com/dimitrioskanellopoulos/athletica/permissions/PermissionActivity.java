package com.dimitrioskanellopoulos.athletica.permissions;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.dimitrioskanellopoulos.athletica.R;

import java.util.ArrayList;

public class PermissionActivity extends WearableActivity {

    private static final String TAG = "PermissionActivity";

    /**
     * The permissions we would like to have. Wanted because they are not actually needed
     */
    private final  ArrayList<String> wantedPermissions = new ArrayList<>();

    /**
     * The broadcast signal for our permission request
     */
    private static final int PERMISSION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wantedPermissions.clear();
        wantedPermissions.add(getIntent().getExtras().getString("permission"));

        // If there are no permissions missing what are we doing here?
        if (!hasMissingPermissions()){
            Log.w(TAG, "Launched with no missing permissions");
            finish();
            return;
        }

        setContentView(R.layout.activity_watch_face_permissions);
        setAmbientEnabled();
    }

    /**
     * When he clicks on the layouts
     */
    public void onClickEnablePermission(View view) {
        Log.d(TAG, "onClickEnablePermission()");
        ActivityCompat.requestPermissions(this, getMissingPermissions(), PERMISSION_REQUEST);
    }

    /**
     * Are there any permissions what we want and we don't have?
     */
    public Boolean hasMissingPermissions(){
        return getMissingPermissions().length != 0;
    }

    /**
     * Permissions that are missing from the wanted permissions
     */
    public String[] getMissingPermissions(){
        ArrayList<String> missingPermissions = new ArrayList<>();
        for (String permission : wantedPermissions) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        return missingPermissions.toArray(new String[missingPermissions.size()]);
    }

    /**
     * When the user makes his choice
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.d(TAG, "onRequestPermissionsResult()");

        // It's not out code
        if (requestCode != PERMISSION_REQUEST) {
            return;
        }

        // We did not have any grant results
        if (grantResults.length <= 0) {
            return;
        }

        // Go over the results. It's a 1:1 array with the permissions
        for (int i = 0; i < permissions.length; i++) {
            // If this permission is granted
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                // Check next this one is granted
                Toast.makeText(this, getResources().getText(R.string.permission_granted), Toast.LENGTH_LONG).show();
                sendPermissionChangedBroadcast(PermissionsHelper.PERMISSIONS_GRANTED, permissions[i]);
                continue;
            }

            // Permission was not granted
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                sendPermissionChangedBroadcast(PermissionsHelper.PERMISSIONS_DENIED_DONT_ASK_AGAIN, permissions[i]);
                //Never ask again selected, or device policy prohibits the app from having that permission.
                // So, disable that feature, or fall back to another situation...
                // user denied flagging NEVER ASK AGAIN
                // you can either enable some fall back,
                // disable features of your app
                // or open another dialog explaining
                // again the permission and directing to
                // the app setting
                continue;
            }
            sendPermissionChangedBroadcast(PermissionsHelper.PERMISSIONS_DENIED, permissions[i]);
            // @todo make per permission text
            // user denied WITHOUT never ask again
            // this is a good place to explain the user
            // why you need the permission and ask if he want
            // to accept it (the rationale)
        }
        finish();
    }

    private void sendPermissionChangedBroadcast(String status, String permission) {
        Intent intent = new Intent(PermissionsHelper.PERMISSIONS_CHANGED_BROADCAST);
        intent.putExtra("permission", permission);
        intent.putExtra("status", status);
        sendBroadcast(intent);
    }
}