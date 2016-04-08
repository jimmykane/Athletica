package com.dimitrioskanellopoulos.activityface;

import android.location.Location;
import android.util.Pair;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;

import java.util.Calendar;

public class SunriseSunsetTimesService {

    public static Pair<String, String> getSunriseAndSunset(Location location, String timezone) {
        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(
                new com.luckycatlabs.sunrisesunset.dto.Location(location.getLatitude(), location.getLongitude()),
                timezone
        );
        String officialSunrise = calculator.getOfficialSunriseForDate(Calendar.getInstance());
        String officialSunset = calculator.getOfficialSunsetForDate(Calendar.getInstance());
        return new Pair<>(officialSunrise, officialSunset);
    }
}
