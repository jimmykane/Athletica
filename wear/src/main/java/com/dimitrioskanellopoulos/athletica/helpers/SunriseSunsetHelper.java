package com.dimitrioskanellopoulos.athletica.helpers;

import android.location.Location;
import android.util.Log;
import android.util.Pair;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;

import java.util.Calendar;

public class SunriseSunsetHelper {
    private static final String TAG = "SunriseSunsetHelper";

    public static Calendar officialSunrise;
    public static Calendar officialSunset;

    public static Pair<String, String> getSunriseAndSunset(Location location, String timezone) {
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(
                new com.luckycatlabs.sunrisesunset.dto.Location(location.getLatitude(), location.getLongitude()),
                timezone
        );
        String officialSunriseString = calculator.getOfficialSunriseForDate(Calendar.getInstance());
        String officialSunsetString = calculator.getOfficialSunsetForDate(Calendar.getInstance());
        officialSunrise = calculator.getOfficialSunriseCalendarForDate(Calendar.getInstance());
        officialSunset = calculator.getOfficialSunsetCalendarForDate(Calendar.getInstance());
        return new Pair<>(officialSunriseString, officialSunsetString);
    }

    public static Boolean isDay(Calendar sunrise, Calendar sunset){
        Calendar now = Calendar.getInstance();
        return now.compareTo(sunrise) > 0 && now.compareTo(sunset) < 0;
    }

    protected SunriseSunsetHelper(){

    }
}
