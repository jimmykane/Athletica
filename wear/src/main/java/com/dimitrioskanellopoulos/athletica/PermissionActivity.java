package com.dimitrioskanellopoulos.athletica;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class PermissionActivity extends WearableActivity {

    private static final String TAG = "PermissionActivity";

    private static final String[] requiredPermissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BODY_SENSORS};

    private static final int PERMISSION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_face_permissions);
        //setAmbientEnabled();
    }

    public void onClickEnablePermission(View view) {
        Log.d(TAG, "onClickEnablePermission()");
        requestPermissions();
    }

    public void requestPermissions() {
        String[] missingPermissions = findMissingPermissions();
        if (missingPermissions.length == 0){
            finish();
            return;
        }
        ActivityCompat.requestPermissions(this, missingPermissions, PERMISSION_REQUEST);
    }

    public String[] findMissingPermissions() {
        ArrayList<String> missingPermissions = new ArrayList<>();
        for (String permission : requiredPermissions) {
            if (ActivityCompat.checkSelfPermission(
                    getApplicationContext(),
                    permission) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {

                missingPermissions.add(permission);
            }
        }
        return missingPermissions.toArray(new String[missingPermissions.size()]);
    }

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


        for (int i = 0; i < grantResults.length; i++) {

            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                // Check next this one is granted
                Toast.makeText(this, getResources().getText(R.string.permissions_restart_app), Toast.LENGTH_LONG).show();
                continue;
            }

            // Permission was not granted
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                Toast.makeText(this, getResources().getText(R.string.permissions_do_not_ask_again_message), Toast.LENGTH_LONG).show();
                //Never ask again selected, or device policy prohibits the app from having that permission.
                //So, disable that feature, or fall back to another situation...
                // user denied flagging NEVER ASK AGAIN
                // you can either enable some fall back,
                // disable features of your app
                // or open another dialog explaining
                // again the permission and directing to
                // the app setting
            }

            // user denied WITHOUT never ask again
            // this is a good place to explain the user
            // why you need the permission and ask if he want
            // to accept it (the rationale)
        }
        finish();
    }
}