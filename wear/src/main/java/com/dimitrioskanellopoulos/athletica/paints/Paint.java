package com.dimitrioskanellopoulos.athletica.paints;

import android.support.annotation.NonNull;

public class Paint extends AbstractTextPaint {
    @Override
    public void setText(@NonNull String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }
}
