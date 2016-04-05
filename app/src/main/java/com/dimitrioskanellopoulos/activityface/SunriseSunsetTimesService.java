package com.dimitrioskanellopoulos.activityface;

import android.location.Location;
import android.util.Pair;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;

import java.util.Calendar;

public class SunriseSunsetTimesService {

    public static Pair<String, String> getSunriseAndSunset(Location location, String timezone) {

        Double latitude = 42.919532;
        Double longitude = 1.035006;

        if (location != null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }

        SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(
                new com.luckycatlabs.sunrisesunset.dto.Location(latitude, longitude),
                timezone
        );
        String officialSunrise = calculator.getOfficialSunriseForDate(Calendar.getInstance());
        String officialSunset = calculator.getOfficialSunsetForDate(Calendar.getInstance());
        return new Pair<>(officialSunrise, officialSunset);
    }
}
