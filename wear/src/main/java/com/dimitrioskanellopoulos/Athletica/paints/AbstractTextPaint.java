package com.dimitrioskanellopoulos.Athletica.paints;

import android.graphics.Paint;

import com.dimitrioskanellopoulos.Athletica.paints.interfaces.TextPaintInterface;

public abstract class AbstractTextPaint extends Paint implements TextPaintInterface {
    protected String text;
}
