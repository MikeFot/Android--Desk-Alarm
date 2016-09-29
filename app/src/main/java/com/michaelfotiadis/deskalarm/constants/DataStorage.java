package com.michaelfotiadis.deskalarm.constants;

import android.content.Context;

import com.michaelfotiadis.deskalarm.containers.TimeModelInstance;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Locale;

public final class DataStorage {

    private static final Object LOCK = new Object();
    private static volatile DataStorage sInstance;

    // map storing usage data in memory as a String - TimeModelInstance map
    private LinkedHashMap<String, TimeModelInstance> mUsageData;

    private DataStorage() {
        // initialise the SortedMap
        setUsageData(new LinkedHashMap<String, TimeModelInstance>());
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

    public LinkedHashMap<String, TimeModelInstance> getUsageData() {
        return mUsageData;
    }

    private void setUsageData(final LinkedHashMap<String, TimeModelInstance> usageData) {
        this.mUsageData = usageData;
    }

    /**
     * Adds an TimeModelInstance to the Set
     *
     * @param dataInstance TimeModelInstance
     */
    public boolean addToUsageData(final Context context, final TimeModelInstance dataInstance) {
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