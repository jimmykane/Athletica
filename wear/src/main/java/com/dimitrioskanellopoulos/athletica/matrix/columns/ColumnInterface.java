package com.dimitrioskanellopoulos.athletica.matrix.columns;

import android.graphics.Paint;

public interface ColumnInterface {
    void setText(String text);
    void setTextForAmbientMode(String text);
    void setPaint(Paint paint);
    void setHorizontalMargin(Float horizontalMargin);
    String getText();
    String getTextForAmbientMode();
    Paint getPaint();
    Float getHeight();
    Float getWidth();
    Float getHorizontalMargin();
}
