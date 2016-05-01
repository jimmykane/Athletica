package com.dimitrioskanellopoulos.athletica.paints.interfaces;

import android.support.annotation.NonNull;

public interface TextPaintInterface {
    void setText(@NonNull String text);

    void inAmbientMode(@NonNull Boolean inAmbient);

    Boolean isInAmbientMode();

    String getText();
}
