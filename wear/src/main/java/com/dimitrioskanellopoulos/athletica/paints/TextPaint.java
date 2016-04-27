package com.dimitrioskanellopoulos.athletica.paints;

import android.graphics.Paint;
import android.support.annotation.NonNull;

import com.dimitrioskanellopoulos.athletica.paints.interfaces.TextPaintInterface;

public class TextPaint extends Paint implements TextPaintInterface {
    protected String text;
    @Override
    public void setText(@NonNull String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }
}
