package com.michaelfotiadis.deskalarm.constants;

import android.content.Context;

import com.michaelfotiadis.deskalarm.containers.TimeModel;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Locale;

public final class DataStorage {

    private static final Object LOCK = new Object();
    private static volatile DataStorage sInstance;

    // map storing usage data in memory as a String - TimeModel map
    private LinkedHashMap<String, TimeModel> mUsageData;

    private DataStorage() {
        // initialise the SortedMap
        setUsageData(new LinkedHashMap<String, TimeModel>());
    }

    public static synchronized DataStorage getInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = new DataStorage();
                }
            }
        }
        return sInstance;
    }

    private static String getSystemTimeFormat(final Context context, final Calendar calendar) {
        // Gets system TF
        final DateFormat tf = android.text.format.DateFormat.getTimeFormat(context);
        return tf.format(calendar.getTime());
    }

    public LinkedHashMap<String, TimeModel> getUsageData() {
        return mUsageData;
    }

    private void setUsageData(final LinkedHashMap<String, TimeModel> usageData) {
        this.mUsageData = usageData;
    }

    /**
     * Adds an TimeModel to the Set
     *
     * @param dataInstance TimeModel
     */
    public boolean addToUsageData(final Context context, final TimeModel dataInstance) {
        if (getUsageData().containsValue(dataInstance)) {
            // skip this if the object already exists
            return false;
        }

        final String timeString = getSystemTimeFormat(context, dataInstance.getCalendarLogged());

        // add the data to the SortedMap
        getUsageData().put(timeString, dataInstance);
        AppLog.i(String.format(Locale.UK, "Added data %s - %d", timeString, dataInstance.getTimeLogged()));
        return true;
    }

}