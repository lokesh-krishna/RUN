package com.example.tobias.run.helpers;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Contains methods to handle datetime and conversion between TrackedRun values in String format and values in database input format.
 */
public class DateManager {

    /**
     *
     * @param distance in d*100
     * @param unit
     * @return distance in the format of "1 km/mi"
     */
    public static String distanceToString(double distance, String unit){
        DecimalFormat df = new DecimalFormat("0.00");
        distance = distance / 100; //Convert to km from m
        String distanceText = df.format(distance) + " " + unit;
        return distanceText;
    }


    /**
     *
     * @param time in unix timestamp format
     * @return time in format hh:mm:ss
     */
    public static String timeToString(long time){
        //Create period to transform time in millis to formatted time.
        Period period = new Period(time);

        String timeText = new StringBuilder()
                .append(String.format(Locale.US, "%02d", period.getHours()))
                .append(":")
                .append(String.format(Locale.US, "%02d", period.getMinutes()))
                .append(":")
                .append(String.format(Locale.US, "%02d", period.getSeconds())).toString();

        return timeText;
    }

    /**
     *
     * @param date in unix timestamp
     * @return date in format "E, e/d/y"
     */
    public static String dateToString(long date){
        DateTimeFormatter formatter = DateTimeFormat.forPattern("E, d/M/y");
        //Unix time is in milli, DateTime is in sec, so multiply by 1000 to convert
        String dateText = formatter.print(new DateTime(date * 1000L));
        return dateText;
    }

    /**
     *
     * @param distance in format d*100
     * @return distance in format 1 km/mi
     */
    public static double distanceToDouble(String distance){
        //Double.ValueOf wont parse comma, but will parse dot.
        distance = distance.replace(",", ".");
        //Remove km or mi
        distance = distance.replace("km", "").replace("mi", "").trim();
        //Convert to whole number by moving comma and return
        return (Double.valueOf(distance) * 100);
    }

    /**
     *
     * @param time in hh:mm:ss
     * @return time in unix timestamp
     */
    public static long timeToUnix(String time){
        String[] timeUnits = new String[3];
        //Split time into hours seconds minutes
        timeUnits = time.split(":");
        Period timePeriod = new Period()
                .withHours(Integer.valueOf(timeUnits[0]))
                .withMinutes(Integer.valueOf(timeUnits[1]))
                .withSeconds(Integer.valueOf(timeUnits[2]));
        //Return time in millis
        return timePeriod.toStandardDuration().getMillis();
    }

    /**
     *
     * @param date in format "E, e/MMM/YYYY"
     * @return date in unix timestamp
     */
    public static long dateToUnix(String date){
        //Remove irrelevant day information
        date = date.split(",")[1].trim();
        String[] dateUnits = new String[3];
        //Split into year month day
        dateUnits = date.split("/");
        DateTime dateTime = new DateTime()
                .withYear(Integer.valueOf(dateUnits[2]))
                .withMonthOfYear(Integer.valueOf(dateUnits[1]))
                .withDayOfMonth(Integer.valueOf(dateUnits[0]));

        return dateTime.getMillis() / 1000;
    }

    public static long getStartOfWeek(){
        long firstDayOfWeekTimestamp = new DateTime().withDayOfWeek(1).getMillis() / 1000;
        return firstDayOfWeekTimestamp;
    }

    public static long getEndOfWeek(){
        long lastDayOfWeekTimestamp = new DateTime().withDayOfWeek(7).getMillis() / 1000;
        return lastDayOfWeekTimestamp;
    }

    public static long getStartOfMonth(){
        long firstDayOfMonthTimestamp = new DateTime().dayOfMonth().withMinimumValue().getMillis() / 1000;
        return firstDayOfMonthTimestamp;
    }

    public static long getEndOfMonth(){
        long lastDayOfMonthTimestamp = new DateTime().dayOfMonth().withMaximumValue().getMillis() / 1000;
        return lastDayOfMonthTimestamp;
    }

    public static long getStartOfYear(){
        long firstDayOfYearTimestamp = new DateTime().dayOfYear().withMinimumValue().getMillis() / 1000;
        return firstDayOfYearTimestamp;
    }

    public static long getEndOfYear(){
        long lastDayOfYearTimestamp = new DateTime().dayOfYear().withMaximumValue().getMillis() / 1000;
        return lastDayOfYearTimestamp;
    }





}