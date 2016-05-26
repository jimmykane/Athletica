package com.dimitrioskanellopoulos.athletica.helpers;

import android.content.Context;

public class PackageHelper {
    public static Boolean isPro(Context context) {
        return context.getPackageName().contains(".pro");
    }

}
