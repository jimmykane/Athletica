package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dimitrioskanellopoulos.athletica.grid.columns.abstracts.GoogleApiColumn;
import com.dimitrioskanellopoulos.athletica.helpers.EmulatorHelper;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Subscription;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.ListSubscriptionsResult;

import java.util.List;
import java.util.Locale;


public class GoogleFitStepsColumn extends GoogleApiColumn implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<DailyTotalResult> {
    private final static String TAG = "GoogleFitStepsColumn";
    private static GoogleApiClient googleApiClient;
    private PendingResult<DailyTotalResult> stepsResult;
    private Boolean hasRegisteredReceivers = false;

    public GoogleFitStepsColumn(Context context, Typeface paintTypeface, Float paintTextSize, int paintColor, Boolean visible, Boolean ambientMode) {
        super(context, paintTypeface, paintTextSize, paintColor, visible, ambientMode);
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
        googleApiClient.connect();

        if (EmulatorHelper.isEmulator()) {
            setText(withSuffix(1801));
        }
    }

    private static String withSuffix(long count) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format(Locale.getDefault(), "%.1f%c",
                count / Math.pow(1000, exp),
                "kMGTPE".charAt(exp - 1));
    }

    @Override
    public void onResult(@NonNull DailyTotalResult dailyTotalResult) {
        Log.d(TAG, "onResult(): " + dailyTotalResult);
        hasRegisteredReceivers = false;
        if (dailyTotalResult.getStatus().isSuccess()) {
            List<DataPoint> points = dailyTotalResult.getTotal().getDataPoints();

            if (!points.isEmpty()) {
                Integer stepsTotal = points.get(0).getValue(Field.FIELD_STEPS).asInt();
                setText(withSuffix(stepsTotal));
                Log.d(TAG, "steps updated: " + stepsTotal);
            }
        } else {
            Log.e(TAG, "onResult() failed! " + dailyTotalResult.getStatus().getStatusMessage());
        }
    }

    @Override
    public void setAmbientMode(Boolean ambientMode) {
        super.setAmbientMode(ambientMode);
        if (!ambientMode && !hasRegisteredReceivers()) {
            registerReceivers();
        }
    }

    @Override
    public void runTasks() {
        super.runTasks();
        registerReceivers();
    }

    /**
     * Subscribe to an available {@link DataType}. Subscriptions can exist across application
     * instances (so data is recorded even after the application closes down).  When creating
     * a new subscription, it may already exist from a previous invocation of this app.  If
     * the subscription already exists, the method is a no-op.  However, you can check this with
     * a special success code.
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

    private void dumpStepsSubscriptionsList() {
        Fitness.RecordingApi.listSubscriptions(googleApiClient, DataType.TYPE_STEP_COUNT_DELTA)
                // Create the callback to retrieve the list of subscriptions asynchronously.
                .setResultCallback(new ResultCallback<ListSubscriptionsResult>() {
                    @Override
                    public void onResult(@NonNull ListSubscriptionsResult listSubscriptionsResult) {
                        for (Subscription sc : listSubscriptionsResult.getSubscriptions()) {
                            DataType dt = sc.getDataType();
                            Log.d(TAG, "Active subscription for data type: " + dt.getName());
                        }
                    }
                });
    }

    /**
     * Cancel the ACTIVITY_SAMPLE subscription by calling unsubscribe on that {@link DataType}.
     */
    private void unsubscribeFromSteps() {
        final String dataTypeStr = DataType.TYPE_STEP_COUNT_DELTA.toString();
        Log.i(TAG, "Unsubscribing from data type: " + dataTypeStr);

        // Invoke the Recording API to unsubscribe from the data type and specify a callback that
        // will check the result.
        Fitness.RecordingApi.unsubscribe(googleApiClient, DataType.TYPE_ACTIVITY_SAMPLE)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Successfully unsubscribed for data type: " + dataTypeStr);
                        } else {
                            // Subscription not removed
                            Log.i(TAG, "Failed to unsubscribe for data type: " + dataTypeStr);
                        }
                    }
                });
    }

    @Override
    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    @Override
    public void registerReceivers() {
        if (!getGoogleApiClient().isConnected()) {
            Log.d(TAG, "Google api client is not connected wont register pending intent for getTotalSteps()");
            return;
        }
        Log.d(TAG, "Register pending intent for getTotalSteps()");
        hasRegisteredReceivers = true;
        subscribeToSteps();
        stepsResult = Fitness.HistoryApi.readDailyTotal(googleApiClient, DataType.TYPE_STEP_COUNT_DELTA);
        stepsResult.setResultCallback(this);
    }

    @Override
    public void unRegisterReceivers() {
        Log.d(TAG, "Cancel pending intent for getTotalSteps()");
        stepsResult.cancel();
    }

    @Override
    public Boolean hasRegisteredReceivers() {
        return hasRegisteredReceivers;
    }
}
