package com.dimitrioskanellopoulos.athletica.paints.interfaces;

import com.dimitrioskanellopoulos.athletica.paints.TextPaint;

public interface SensorPaintInterface extends TextPaintInterface {
    String getIcon();
    String getUnits();
    void setIconTextPaint(TextPaint iconTextPaint);
    TextPaint getIconTextPaint();
}
