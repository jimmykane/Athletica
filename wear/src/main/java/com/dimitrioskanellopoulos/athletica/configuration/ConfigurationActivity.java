package com.dimitrioskanellopoulos.athletica.configuration;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.dimitrioskanellopoulos.athletica.helpers.EmulatorHelper;
import com.dimitrioskanellopoulos.athletica.R;


// @todo add antialias support
public class ConfigurationActivity extends WearableActivity {

    private Switch switchTimeFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration);
        if (EmulatorHelper.isEmulator()){
            setContentView(R.layout.configuration_dev);

        }
        setAmbientEnabled();

        switchTimeFormat = (Switch) findViewById(R.id.switch_24_hour_clock);

        switchTimeFormat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if(isChecked){
                    Toast.makeText(getApplicationContext(), "On", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Off", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
        });
    }

    public void onClickAtLayout(View view) {
        finish();
    }
}
