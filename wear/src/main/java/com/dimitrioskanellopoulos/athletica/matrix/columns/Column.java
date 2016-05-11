package com.dimitrioskanellopoulos.athletica.matrix.columns;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

public class Column implements ColumnInterface {
    private final Paint paint = new Paint();
    private Boolean ambientMode = false;
    // @todo fix this
    private String text = "";
    private Float horizontalMargin = 0.0f;

    public Column(Typeface paintTypeface, Float paintTextSize ,int paintColor){
        getPaint().setTypeface(paintTypeface);
        getPaint().setTextSize(paintTextSize);
        getPaint().setColor(paintColor);
        setAmbientMode(false);
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public void setHorizontalMargin(Float horizontalMargin) {
        this.horizontalMargin = horizontalMargin;
    }

    @Override
    public void setAmbientMode(Boolean ambientMode) {
        getPaint().setColor(Color.WHITE);
        getPaint().setAntiAlias(!ambientMode);
        this.ambientMode = ambientMode;
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
        Rect textBounds = new Rect();
        getPaint().getTextBounds(getText(), 0, getText().length(), textBounds);
        return (float) textBounds.height();
    }

    @Override
    public Float getWidth() {
        // If not text no width;
        return getPaint().measureText(getText());
    }

    @Override
    public Float getHorizontalMargin() {
        return horizontalMargin;
    }

    @Override
    public Boolean isInAmbientMode() {
        return ambientMode;
    }
}