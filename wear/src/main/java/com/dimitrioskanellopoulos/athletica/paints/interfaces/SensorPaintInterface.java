package com.dimitrioskanellopoulos.athletica.paints.interfaces;

import com.dimitrioskanellopoulos.athletica.paints.IconTextPaint;

public interface SensorPaintInterface extends TextPaintInterface {
    String getIcon();
    String getUnits();
    void setIconTextPaint(IconTextPaint iconTextPaint);
    IconTextPaint getIconTextPaint();
}
