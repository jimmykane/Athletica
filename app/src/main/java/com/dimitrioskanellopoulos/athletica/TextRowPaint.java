package com.dimitrioskanellopoulos.athletica;

import android.graphics.Paint;

/**
 * A TextRowPaint is a normal paint that has just an extra placeholder for text
 */
public class TextRowPaint extends Paint{

    private String text;

    public void setText(String text){
        this.text = text;
    }

    public String getText(){
        return text;
    }
}
