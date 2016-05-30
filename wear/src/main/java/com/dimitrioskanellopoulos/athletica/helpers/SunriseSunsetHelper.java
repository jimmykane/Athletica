package com.dimitrioskanellopoulos.athletica.helpers;

import android.location.Location;
import android.util.Log;
import android.util.Pair;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;

import java.util.Calendar;

public class SunriseSunsetHelper {
    private static final String TAG = "SunriseSunsetHelper";

    private static Calendar officialSunrise;
    private static Calendar officialSunset;

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

    public static Boolean isDay(){
        if (getLastKnownSunriseSunset() == null){
            Log.d(TAG, "Defaulting to day" );
            return true;
        }
        Calendar now = Calendar.getInstance();
        Boolean isDay =  now.compareTo(officialSunrise) > 0 && now.compareTo(officialSunset) < 0;
        Log.d(TAG, "It is day " + isDay.toString());
        return isDay;
    }

    public static Pair<Calendar, Calendar> getLastKnownSunriseSunset(){
        if (officialSunrise == null || officialSunset == null){
            return null;
        }
        return new Pair<>(officialSunrise, officialSunset);
    }

    protected SunriseSunsetHelper(){

    }
}
