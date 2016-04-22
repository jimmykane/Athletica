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

import com.dimitrioskanellopoulos.athletica.R;

/**
 * Simple Activity for displaying Calendar Permission Rationale to user.
 */
public class BodySensorsPermissionActivity extends WearableActivity {

    private static final String TAG = "PermissionActivity";

    /* Id to identify permission request for location. */
    private static final int PERMISSION_REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_sensors_watch_face_permission);
        setAmbientEnabled();
    }

    public void onClickEnablePermission(View view) {
        requestPermissions();
    }


    public void requestPermissions() {
        Log.d(TAG, "onClickEnablePermission()");
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.BODY_SENSORS},
                PERMISSION_REQUEST_LOCATION);
    }

    /*
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.d(TAG, "onRequestPermissionsResult()");

        // @todo fix this shit
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Do the stuff that requires permission...
                    finish();
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    // Should we show an explanation?
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BODY_SENSORS)) {
                        Toast.makeText(this, "Please go to settings and allow this permission if you want to get heart rate", Toast.LENGTH_LONG).show();
                        //Never ask again selected, or device policy prohibits the app from having that permission.
                        //So, disable that feature, or fall back to another situation...
                        // user denied flagging NEVER ASK AGAIN
                        // you can either enable some fall back,
                        // disable features of your app
                        // or open another dialog explaining
                        // again the permission and directing to
                        // the app setting
                        finish();
                        return;
                    }
                    Toast.makeText(this, "Please allow body sensor permission for heart rate", Toast.LENGTH_LONG).show();
                    requestPermissions();
                    finish();
                    // user denied WITHOUT never ask again
                    // this is a good place to explain the user
                    // why you need the permission and ask if he want
                    // to accept it (the rationale)
                }
            }
        }
    }
}