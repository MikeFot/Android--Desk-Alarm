package com.michaelfotiadis.deskalarm.constants;

import android.content.Context;

import com.michaelfotiadis.deskalarm.containers.ErgoTimeDataInstance;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Locale;

public final class Singleton {
    // map storing usage data in memory as a String - ErgoTimeDataInstance map
    private LinkedHashMap<String, ErgoTimeDataInstance> usageData;

    private Singleton() {
        // initialise the SortedMap
        setUsageData(new LinkedHashMap<String, ErgoTimeDataInstance>());
    }

    public static Singleton getInstance() {
        return SingletonHolder.instance;
    }

    public LinkedHashMap<String, ErgoTimeDataInstance> getUsageData() {
        return usageData;
    }

    private void setUsageData(final LinkedHashMap<String, ErgoTimeDataInstance> usageData) {
        this.usageData = usageData;
    }

    /**
     * Adds an ErgoTimeDataInstance to the Set
     *
     * @param dataInstance ErgoTimeDataInstance
     */
    public boolean addToUsageData(final Context context, final ErgoTimeDataInstance dataInstance) {
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

    private static String getSystemTimeFormat(final Context context, final Calendar calendar) {
        // Gets system TF
        final DateFormat tf = android.text.format.DateFormat.getTimeFormat(context);
        return tf.format(calendar.getTime());
    }

    private static final class SingletonHolder {
        private static final Singleton instance = new Singleton();
    }
}