package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.graphics.Paint;

public interface ColumnInterface {
    void setAmbientMode(Boolean ambientMode);

    String getText();

    void setText(String text);

    Integer getTextDefaultColor();

    void setTextDefaultColor(Integer color);

    Paint getPaint();

    Float getHeight();

    Float getWidth();

    Float getHorizontalMargin();

    void setHorizontalMargin(Float horizontalMargin);

    Boolean isInAmbientMode();
}
