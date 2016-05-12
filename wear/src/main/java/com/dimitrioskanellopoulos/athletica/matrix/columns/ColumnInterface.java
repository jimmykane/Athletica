package com.dimitrioskanellopoulos.athletica.matrix.columns;

import android.graphics.Paint;

public interface ColumnInterface {
    void setText(String text);

    void setTextDefaultColor(Integer color);

    void setHorizontalMargin(Float horizontalMargin);

    void setAmbientMode(Boolean ambientMode);

    String getText();

    Integer getTextDefaultColor();

    Paint getPaint();

    Float getHeight();

    Float getWidth();

    Float getHorizontalMargin();

    Boolean isInAmbientMode();
}
