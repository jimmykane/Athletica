package com.dimitrioskanellopoulos.athletica.matrix.columns;

import android.graphics.Paint;

public class Column implements ColumnInterface {
    private String text;
    private Paint paint;
    private Float horizontalMargin;

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    @Override
    public void setHorizontalMargin(Float horizontalMargin) {
        this.horizontalMargin = horizontalMargin;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Paint getPaint() {
        return paint;
    }
}
