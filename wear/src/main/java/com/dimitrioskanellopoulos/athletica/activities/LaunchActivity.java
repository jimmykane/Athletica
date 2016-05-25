package com.dimitrioskanellopoulos.athletica.activities;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;

import com.dimitrioskanellopoulos.athletica.WatchFaceService;

public class LaunchActivity extends WearableActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER)
                .putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                        new ComponentName(getPackageName(),
                                WatchFaceService.class.getName()));
        startActivity(intent);

        finish();
    }
}
