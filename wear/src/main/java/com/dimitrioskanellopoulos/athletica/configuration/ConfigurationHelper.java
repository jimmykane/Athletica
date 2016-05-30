package com.dimitrioskanellopoulos.athletica.configuration;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dimitrioskanellopoulos.athletica.helpers.SensorHelper;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;

public final class ConfigurationHelper {
    /**
     * The {@link DataMap} key for {@link com.dimitrioskanellopoulos.athletica.WatchFaceService} time format.
     */
    public static final String KEY_TIME_FORMAT = "TIME_FORMAT";
    /**
     * The {@link DataMap} key for {@link com.dimitrioskanellopoulos.athletica.WatchFaceService} day_name.
     */
    public static final String KEY_DATE_NAMES = "DATE_NAMES";
    /**
     * The {@link DataMap} key for {@link com.dimitrioskanellopoulos.athletica.WatchFaceService} interlace.
     */
    public static final String KEY_INTERLACE = "INTERLACE";
    /**
     * The {@link DataMap} key for {@link com.dimitrioskanellopoulos.athletica.WatchFaceService} invert black and white.
     */
    public static final String KEY_INVERT_BLACK_AND_WHITE = "KEY_INVERT_BLACK_AND_WHITE";
    /**
     * The {@link DataMap} key for {@link com.dimitrioskanellopoulos.athletica.WatchFaceService} automatic day or night mode.
     */
    public static final String KEY_AUTO_DAY_NIGHT_MODE = "KEY_AUTO_DAY_NIGHT_MODE";
    /**
     * The {@link DataMap} key for {@link com.dimitrioskanellopoulos.athletica.WatchFaceService} sensors.
     */
    public static final String KEY_ENABLED_SENSORS = "KEY_ENABLED_SENSORS";
    /**
     * The default time format
     */
    public static final Boolean TIME_FORMAT_DEFAULT = true;
    /**
     * The default if to show the name
     */
    public static final Boolean DATE_NAMES_DEFAULT = false;
    /**
     * The default interlace
     */
    public static final Boolean INTERLACE_DEFAULT = true;
    /**
     * The default to invert black and white
     */
    public static final Boolean INVERT_BLACK_AND_WHITE = false;
    /**
     * The default to invert black and white
     */
    public static final Boolean AUTO_DAY_NIGHT_MODE = false;
    /**
     * The path for the {@link DataItem} containing {@link com.dimitrioskanellopoulos.athletica.WatchFaceService} configuration.
     */
    public static final String PATH_WITH_FEATURE = "/athletica/config";
    private static final String TAG = "ConfigurationHelper";

    /**
     * Asynchronously fetches the current config {@link DataMap} for {@link com.dimitrioskanellopoulos.athletica.WatchFaceService}
     * and passes it to the given callback.
     * <p/>
     * If the current config {@link DataItem} doesn't exist, it isn't created and the callback
     * receives an empty DataMap.
     */
    public static void fetchConfigDataMap(final GoogleApiClient client,
                                          final FetchConfigDataMapCallback callback) {
        Wearable.NodeApi.getLocalNode(client).setResultCallback(
                new ResultCallback<NodeApi.GetLocalNodeResult>() {
                    @Override
                    public void onResult(@NonNull NodeApi.GetLocalNodeResult getLocalNodeResult) {
                        String localNode = getLocalNodeResult.getNode().getId();
                        Uri uri = new Uri.Builder()
                                .scheme("wear")
                                .path(ConfigurationHelper.PATH_WITH_FEATURE)
                                .authority(localNode)
                                .build();
                        Wearable.DataApi.getDataItem(client, uri)
                                .setResultCallback(new DataItemResultCallback(callback));
                    }
                }
        );
    }

    /**
     * Overwrites (or sets, if not present) the keys in the current config {@link DataItem} with
     * the ones appearing in the given {@link DataMap}. If the config DataItem doesn't exist,
     * it's created.
     * <p/>
     * It is allowed that only some of the keys used in the config DataItem appear in
     * {@code configKeysToOverwrite}. The rest of the keys remains unmodified in this case.
     */
    public static void overwriteKeysInConfigDataMap(final GoogleApiClient googleApiClient,
                                                    final DataMap configKeysToOverwrite) {

        ConfigurationHelper.fetchConfigDataMap(googleApiClient,
                new FetchConfigDataMapCallback() {
                    @Override
                    public void onConfigDataMapFetched(DataMap currentConfig) {
                        DataMap overwrittenConfig = new DataMap();
                        overwrittenConfig.putAll(currentConfig);
                        overwrittenConfig.putAll(configKeysToOverwrite);
                        ConfigurationHelper.putConfigDataItem(googleApiClient, overwrittenConfig);
                    }
                }
        );
    }

    /**
     * Overwrites the current config {@link DataItem}'s {@link DataMap} with {@code newConfig}.
     * If the config DataItem doesn't exist, it's created.
     */
    public static void putConfigDataItem(GoogleApiClient googleApiClient, DataMap newConfig) {
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(PATH_WITH_FEATURE);
        putDataMapRequest.setUrgent();
        DataMap configToPut = putDataMapRequest.getDataMap();
        configToPut.putAll(newConfig);
        Wearable.DataApi.putDataItem(googleApiClient, putDataMapRequest.asPutDataRequest())
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                        Log.d(TAG, "putDataItem result status: " + dataItemResult.getStatus());
                    }
                });
    }

    public static void setDefaultValuesForMissingConfigKeys(Context context, DataMap config) {
        addBooleanKeyIfMissing(config, KEY_TIME_FORMAT, TIME_FORMAT_DEFAULT);
        addBooleanKeyIfMissing(config, KEY_DATE_NAMES, DATE_NAMES_DEFAULT);
        addBooleanKeyIfMissing(config, KEY_INTERLACE, INTERLACE_DEFAULT);
        addBooleanKeyIfMissing(config, KEY_INVERT_BLACK_AND_WHITE, INVERT_BLACK_AND_WHITE);
        addBooleanKeyIfMissing(config, KEY_AUTO_DAY_NIGHT_MODE, AUTO_DAY_NIGHT_MODE);
        addIntegerArrayListKeyIfMissing(config, KEY_ENABLED_SENSORS, SensorHelper.getApplicationDeviceSupportedSensors(context));
    }

    private static void addBooleanKeyIfMissing(DataMap config, String key, Boolean value) {
        if (!config.containsKey(key)) {
            config.putBoolean(key, value);
        }
    }

    private static void addIntegerArrayListKeyIfMissing(DataMap config, String key, ArrayList<Integer> value) {
        if (!config.containsKey(key) || config.getIntegerArrayList(key) == null) {
            config.putIntegerArrayList(key, value);
        }
    }

    /**
     * Callback interface to perform an action with the current config {@link DataMap} for
     * {@link com.dimitrioskanellopoulos.athletica.WatchFaceService}.
     */
    public interface FetchConfigDataMapCallback {
        /**
         * Callback invoked with the current config {@link DataMap} for
         * {@link com.dimitrioskanellopoulos.athletica.WatchFaceService}.
         */
        void onConfigDataMapFetched(DataMap config);
    }

    private static class DataItemResultCallback implements ResultCallback<DataApi.DataItemResult> {

        private final FetchConfigDataMapCallback mCallback;

        public DataItemResultCallback(FetchConfigDataMapCallback callback) {
            mCallback = callback;
        }

        @Override
        public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
            if (dataItemResult.getStatus().isSuccess()) {
                if (dataItemResult.getDataItem() != null) {
                    DataItem configDataItem = dataItemResult.getDataItem();
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(configDataItem);
                    DataMap config = dataMapItem.getDataMap();
                    mCallback.onConfigDataMapFetched(config);
                } else {
                    mCallback.onConfigDataMapFetched(new DataMap());
                }
            }
        }
    }
}
