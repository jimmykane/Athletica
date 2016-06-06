package com.dimitrioskanellopoulos.athletica.grid.columns.abstracts;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.dimitrioskanellopoulos.athletica.grid.columns.Column;
import com.dimitrioskanellopoulos.athletica.grid.columns.interfaces.GoogleApiColumnInterface;
import com.dimitrioskanellopoulos.athletica.grid.columns.interfaces.ReceiverColumnInterface;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public abstract class GoogleApiColumn extends Column implements GoogleApiColumnInterface, ReceiverColumnInterface,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private final static String TAG = "GoogleApiColumn";

    public GoogleApiColumn(Context context, Typeface paintTypeface, Float paintTextSize, int paintColor) {
        super(context, paintTypeface, paintTextSize, paintColor);
    }

    @Override
    public void setIsVisible(Boolean isVisible) {
        super.setIsVisible(isVisible);
        if (isVisible) {
            if (!getGoogleApiClient().isConnected()) {
                getGoogleApiClient().connect();
            }
        } else {
            if (getGoogleApiClient().isConnected()) {
                if (hasRegisteredReceivers()){
                    unRegisterReceivers();
                }
                getGoogleApiClient().disconnect();
            }
        }
    }

    @Override
    public void destroy() {
        if (hasRegisteredReceivers()){
            unRegisterReceivers();
        }
        if (getGoogleApiClient().isConnected()) {
            getGoogleApiClient().disconnect();
        }
        super.destroy();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Google Api Connected");
        registerReceivers();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Google Api connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "Google Api connection failed");
    }
}
