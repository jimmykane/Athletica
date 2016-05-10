package com.dimitrioskanellopoulos.athletica.matrix.columns;

import android.graphics.Paint;
import android.graphics.Rect;

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

    @Override
    public Float getHeight() {
        // If no text no height for now
        if (getText() == null){
            return 0.0f;
        }
        Rect textBounds = new Rect();
        getPaint().getTextBounds(getText(), 0, getText().length(), textBounds);
        return (float) textBounds.height();
    }
}
