package com.dimitrioskanellopoulos.activityface;

import android.support.wearable.watchface.CanvasWatchFaceService;

public class SimpleWatchFaceService extends CanvasWatchFaceService {

    @Override
    public Engine onCreateEngine() {
        return new SimpleEngine();
    }

    private class SimpleEngine extends CanvasWatchFaceService.Engine {

    }
}