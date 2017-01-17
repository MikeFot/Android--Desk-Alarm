package com.michaelfotiadis.deskalarm.containers;

import com.michaelfotiadis.deskalarm.constants.AppConstants;
import com.michaelfotiadis.deskalarm.utils.PrimitiveConversions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Custom Object for storing data related to logged time
 *
 * @author Michael Fotiadis
 */
public class TimeModel {
    private final long mTimeElapsed;
    private final Calendar mCalendarLogged;

    public TimeModel(final long timeStarted) {
        final Long currentTime = Calendar.getInstance().getTimeInMillis();
        mCalendarLogged = Calendar.getInstance();
        mTimeElapsed = TimeUnit.MILLISECONDS.toMinutes(currentTime - timeStarted);
    }

    public TimeModel(final long timeLogged, final long timeElapsed) {

        mCalendarLogged = Calendar.getInstance();
        mCalendarLogged.setTimeInMillis(timeLogged);
        mTimeElapsed = (int) timeElapsed;
    }

    public TimeModel(final int timeElapsed, final Calendar calendarLogged) {
        mTimeElapsed = timeElapsed;
        mCalendarLogged = calendarLogged;
    }

    public String getTimeString() {
        final int hours = getCalendarLogged().get(Calendar.HOUR_OF_DAY);
        final int minutes = getCalendarLogged().get(Calendar.MINUTE);
        final int seconds = getCalendarLogged().get(Calendar.SECOND);

        final int[] timeArray = new int[]{hours, minutes, seconds};

        return PrimitiveConversions.getTimeStringFromIntegerArray(timeArray);


    }

    public long getTimeLogged() {
        return mTimeElapsed;
    }

    public Calendar getCalendarLogged() {
        return mCalendarLogged;
    }

    public String getExtendedDateFormat() {
        SimpleDateFormat format = new SimpleDateFormat(AppConstants.EXTENDED_DATE_FORMAT_STRING, Locale.getDefault());
        return format.format(mCalendarLogged.getTime());
    }


    public String getSimpleDateFormat() {
        SimpleDateFormat format = new SimpleDateFormat(AppConstants.SIMPLE_DATE_FORMAT_STRING, Locale.getDefault());
        return format.format(mCalendarLogged.getTime());
    }

    public String toOutputString() {
        StringBuilder sb = new StringBuilder();

        sb.append(mCalendarLogged.getTimeInMillis());
        sb.append(',');
        sb.append(mTimeElapsed);
        // add a line separator
        sb.append(System.getProperty("line.separator"));

        return sb.toString();
    }

}
