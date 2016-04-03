package com.dimitrioskanellopoulos.activityface;

import android.util.Pair;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;

import java.util.Calendar;

public class SunriseSunsetTimesService {

    private static String latitude = "42.919532";
    private static String longitude = "1.035006";

    private static Location location = new com.luckycatlabs.sunrisesunset.dto.Location(Double.parseDouble(latitude), Double.parseDouble(longitude));

    public static Pair<String, String> getSunriseAndSunset() {
        String timezone = "Europe/Paris";
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, timezone);
        String officialSunrise = calculator.getOfficialSunriseForDate(Calendar.getInstance());
        String officialSunset = calculator.getOfficialSunsetForDate(Calendar.getInstance());
        return new Pair<String, String>(officialSunrise, officialSunset);
    }
}
