package com.michaelfotiadis.deskalarm.constants;


/**
 * Class for storing constants used throughout the application
 *
 * @author Michael Fotiadis
 */
public final class AppConstants {

    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    // Calendar Fields
    public static final String EXTENDED_DATE_FORMAT_STRING = "yyyy MM dd HH:mm:ss";

    public static final String SIMPLE_DATE_FORMAT_STRING = "HH:mm:ss";

    // Used to convert milliseconds to minutes
    public static final int FACTOR_MSEC_TO_MINUTES = 60000; // TODO correct is 60000

    public static final int ALARM_NOTIFICATION_ID = 517; // random ID
    // path of the internal storage file
    public static final String INTERNAL_STORAGE_FILE = "user_data";


    private AppConstants() {
        // do not instantiate
    }
}
