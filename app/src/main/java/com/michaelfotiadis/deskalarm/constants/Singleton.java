package com.michaelfotiadis.deskalarm.constants;

import android.content.Context;

import com.michaelfotiadis.deskalarm.containers.ErgoTimeDataInstance;
import com.michaelfotiadis.deskalarm.utils.AppUtils;
import com.michaelfotiadis.deskalarm.utils.Logger;

import java.util.LinkedHashMap;

public class Singleton {
    private final String TAG = "Singleton";

    // map storing usage data in memory as a String - ErgoTimeDataInstance map
    private LinkedHashMap<String, ErgoTimeDataInstance> usageData;

    private Singleton() {
        // initialise the SortedMap
        setUsageData(new LinkedHashMap<String, ErgoTimeDataInstance>());
    }

    private static class SingletonHolder {
        private static final Singleton instance = new Singleton();
    }

    public static Singleton getInstance() {
        return SingletonHolder.instance;
    }

    public LinkedHashMap<String, ErgoTimeDataInstance> getUsageData() {
        return usageData;
    }

    public void setUsageData(LinkedHashMap<String, ErgoTimeDataInstance> usageData) {
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

        String timeString = new AppUtils().getSystemTimeFormat(context, dataInstance.getCalendarLogged());

        // add the data to the SortedMap
        getUsageData().put(timeString, dataInstance);
        Logger.i(TAG, "Added data " + timeString + " - "
                + dataInstance.getTimeLogged());
        return true;
    }
}