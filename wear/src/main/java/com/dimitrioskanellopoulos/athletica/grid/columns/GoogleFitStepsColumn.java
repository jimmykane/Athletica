package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DailyTotalResult;

import java.util.List;


public class GoogleFitStepsColumn extends Column implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<DailyTotalResult> {
    private final static String TAG = "GoogleFitStepsColumn";
    protected static Boolean isTotalStepsRequested = false;
    private static GoogleApiClient googleApiClient;

    public GoogleFitStepsColumn(Context context, Typeface paintTypeface, Float paintTextSize, int paintColor) {
        super(context, paintTypeface, paintTextSize, paintColor);
    }

    @Override
    public void start() {
        super.start();
        // Get a Google API client
        if (googleApiClient == null) {

            googleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Fitness.HISTORY_API)
                    .addApi(Fitness.RECORDING_API)
                    // When user has multiple accounts, useDefaultAccount() allows Google Fit to
                    // associated with the main account for steps. It also replaces the need for
                    // a scope request.
                    .useDefaultAccount()
                    .build();
        }
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }
    }

    @Override
    public void destroy() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.destroy();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Google Api Connected");
        getTotalSteps();
        //subscribeToSteps();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Google Api connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "Google Api connetion failed");
    }

    @Override
    public void onResult(@NonNull DailyTotalResult dailyTotalResult) {
        Log.d(TAG, "onResult(): " + dailyTotalResult);
        isTotalStepsRequested = false;
        if (dailyTotalResult.getStatus().isSuccess()) {
            List<DataPoint> points = dailyTotalResult.getTotal().getDataPoints();

            if (!points.isEmpty()) {
                Integer stepsTotal = points.get(0).getValue(Field.FIELD_STEPS).asInt();
                setText(stepsTotal + "");
                Log.d(TAG, "steps updated: " + stepsTotal);
            }
        } else {
            Log.e(TAG, "onResult() failed! " + dailyTotalResult.getStatus().getStatusMessage());
        }
    }

    @Override
    public void setIsVisible(Boolean isVisible) {
        super.setIsVisible(isVisible);
        if (isVisible) {
            if (googleApiClient != null && !googleApiClient.isConnected()) {
                googleApiClient.connect();
            }
        } else {
            if (googleApiClient != null && googleApiClient.isConnected()) {
                googleApiClient.disconnect();
            }
        }

    }

    @Override
    public void setAmbientMode(Boolean ambientMode) {
        super.setAmbientMode(ambientMode);
        getTotalSteps();
    }

    @Override
    public void runTasks() {
        super.runTasks();
        getTotalSteps();
    }

    private void getTotalSteps() {
        Log.d(TAG, "getTotalSteps()");
        if ((googleApiClient != null)
                && (googleApiClient.isConnected())
                && (!isTotalStepsRequested)) {

            isTotalStepsRequested = true;

            PendingResult<DailyTotalResult> stepsResult =
                    Fitness.HistoryApi.readDailyTotal(
                            googleApiClient,
                            DataType.TYPE_STEP_COUNT_DELTA);

            stepsResult.setResultCallback(this);
        }
    }

    /**
     * @todo enable it from service and setting
     */
    private void subscribeToSteps() {
        Fitness.RecordingApi.subscribe(googleApiClient, DataType.TYPE_STEP_COUNT_DELTA)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            if (status.getStatusCode()
                                    == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                                Log.i(TAG, "Existing subscription for activity detected.");
                            } else {
                                Log.i(TAG, "Successfully subscribed!");
                            }
                        } else {
                            Log.i(TAG, "There was a problem subscribing.");
                        }
                    }
                });
    }
}
