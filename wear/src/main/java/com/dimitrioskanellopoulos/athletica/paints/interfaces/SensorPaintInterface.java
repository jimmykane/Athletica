package com.dimitrioskanellopoulos.athletica.paints.interfaces;

import android.support.annotation.NonNull;

public interface SensorPaintInterface {
    void setText(@NonNull String text);

    String getIcon();

    String getUnits();

    String getText();
}
