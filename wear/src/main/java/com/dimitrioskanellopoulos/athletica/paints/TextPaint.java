package com.dimitrioskanellopoulos.athletica.paints;

import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.dimitrioskanellopoulos.athletica.paints.interfaces.TextPaintInterface;

public class TextPaint extends Paint implements TextPaintInterface {
    protected String text;
    private Boolean isInAmbient = false;

    @Override
    public void setText(@NonNull String text) {
        this.text = text;
    }

    @Override
    public void inAmbientMode(@NonNull Boolean inAmbient) {
        isInAmbient = inAmbient;
        setAntiAlias(!inAmbient);
        if (inAmbient){
            setColor(Color.WHITE);
        }
    }

    @Override
    public Boolean isInAmbientMode() {
        return isInAmbient;
    }

    @Override
    public String getText() {
        return text;
    }
}
