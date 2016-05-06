package com.dimitrioskanellopoulos.athletica.configuration;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;

import com.dimitrioskanellopoulos.athletica.EmulatorHelper;
import com.dimitrioskanellopoulos.athletica.R;


// @todo add antialias support
public class WatchFaceConfigurationActivity extends WearableActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.configuration);
        if (EmulatorHelper.isEmulator()){
            setContentView(R.layout.configuration_dev);

        }
        setAmbientEnabled();
    }

    public void onClickAtLayout(View view) {
        finish();
    }
}
