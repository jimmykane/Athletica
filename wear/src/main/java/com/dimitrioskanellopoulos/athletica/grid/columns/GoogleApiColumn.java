package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.content.Context;
import android.graphics.Typeface;

import com.dimitrioskanellopoulos.athletica.grid.columns.interfaces.GoogleApiColumnInterface;

public abstract class GoogleApiColumn extends Column implements GoogleApiColumnInterface {
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
                getGoogleApiClient().disconnect();
            }
        }
    }

    @Override
    public void destroy() {
        if (getGoogleApiClient().isConnected()) {
            getGoogleApiClient().disconnect();
        }
        super.destroy();
    }
}
