package com.dimitrioskanellopoulos.athletica.activities;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.dimitrioskanellopoulos.athletica.R;

public abstract class AmbientAwareWearableActivity extends WearableActivity implements AmbientAwareWearableActivityInterface {
    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        setAntialiasToLayoutChildren(false);
        super.onEnterAmbient(ambientDetails);
    }

    @Override
    public void onExitAmbient() {
        setAntialiasToLayoutChildren(true);
        super.onExitAmbient();
    }

    private void setAntialiasToLayoutChildren(Boolean antialias){
        LinearLayout layout = getLayout();
        int childCount = layout.getChildCount();
        Log.d("TEST", ""+ childCount);
        for (int i=0; i < childCount; i++){
            View view =  layout.getChildAt(i);
            if (view instanceof TextView){
                ((TextView) view).getPaint().setAntiAlias(antialias);
            }else if (view instanceof Switch){
                ((Switch) view).getPaint().setAntiAlias(antialias);
            }
        }
    }
}
