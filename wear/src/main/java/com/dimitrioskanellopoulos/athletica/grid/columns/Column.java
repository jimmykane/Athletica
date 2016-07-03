package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

import com.dimitrioskanellopoulos.athletica.grid.columns.interfaces.ColumnInterface;

public class Column implements ColumnInterface {
    private final static String TAG = "Column";
    protected final Context context;

    private final Paint paint = new Paint();
    private Boolean ambientMode = false;
    private Boolean shouldAntialiasInAmbientMode = false;
    private Boolean visible = true;
    private Integer baseline = BASELINE_MIDDLE;
    private String text = "\u21bb";
    private Float horizontalMargin = 0.0f;
    private Integer textDefaultColor;

    public Column(Context context, Typeface paintTypeface, Float paintTextSize, int paintColor) {
        this.context = context.getApplicationContext();
        getPaint().setTypeface(paintTypeface);
        getPaint().setTextSize(paintTextSize);
        setTextDefaultColor(paintColor);
    }

    @Override
    public void setAmbientMode(Boolean ambientMode) {
        //getPaint().setColor();
        this.ambientMode = ambientMode;
        getPaint().setAntiAlias(!ambientMode || shouldAntialiasInAmbientMode);
    }

    @Override
    public void setBurnInProtection(Boolean burnInProtection) {
        Log.d(TAG, "BurnInProtection: " + burnInProtection);
    }

    @Override
    public void setLowBitAmbient(Boolean lowBitAmbient) {
        Log.d(TAG, "LowBitAmbientMode: " + lowBitAmbient);
    }

    @Override
    public void shouldAntialiasInAmbientMode(Boolean shouldAntialiasInAmbientMode) {
        Log.d(TAG, "shouldAntialiasInAmbientMode" + shouldAntialiasInAmbientMode.toString());
        this.shouldAntialiasInAmbientMode = shouldAntialiasInAmbientMode;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public Integer getTextDefaultColor() {
        return textDefaultColor;
    }

    @Override
    public void setTextDefaultColor(Integer color) {
        this.textDefaultColor = color;
        getPaint().setColor(textDefaultColor);
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
    public void setHorizontalMargin(Float horizontalMargin) {
        this.horizontalMargin = horizontalMargin;
    }

    @Override
    public Integer getBaseline() {
        return this.baseline;
    }

    @Override
    public void setBaseline(Integer baseline) {
        this.baseline = baseline;
    }

    @Override
    public Boolean isInAmbientMode() {
        return ambientMode;
    }

    @Override
    public Boolean isVisible() {
        return visible;
    }

    @Override
    public void setIsVisible(Boolean isVisible) {
        this.visible = isVisible;
        Log.d(TAG, "Visible " + visible.toString());
    }

    @Override
    public void runTasks() {
        Log.d(TAG, "Running tasks");
    }

    @Override
    public void destroy() {
        Log.d(TAG, "Destroyed");
    }
}