package com.dimitrioskanellopoulos.athletica.grid.columns.interfaces;

import android.graphics.Paint;

public interface ColumnInterface {

    int BASELINE_TOP = 1;
    int BASELINE_MIDDLE = 2;
    int BASELINE_ABSOLUTE_CENTER = 3;
    int BASELINE_PREVIOUS = 4;
    int BASELINE_BOTTOM = 5;
    int BASELINE_OVER_THE_TOP = 6;

    void setAmbientMode(Boolean ambientMode);

    void setBurnInProtection(Boolean burnInProtection);

    void setLowBitAmbient(Boolean lowBitAmbient);

    void shouldAntialiasInAmbientMode(Boolean shouldAntialiasInAmbientMode);

    String getText();

    void setText(String text);

    Integer getTextDefaultColor();

    void setTextDefaultColor(Integer color);

    Paint getPaint();

    Float getHeight();

    Float getWidth();

    Float getHorizontalMargin();

    void setHorizontalMargin(Float horizontalMargin);

    Integer getBaseline();

    void setBaseline(Integer baseline);

    Boolean isInAmbientMode();

    Boolean isVisible();

    void setIsVisible(Boolean visible);

    void runTasks();

    void destroy();
}
