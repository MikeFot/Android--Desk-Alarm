package com.michaelfotiadis.deskalarm.managers;

import android.content.Context;
import android.content.Intent;

import com.michaelfotiadis.deskalarm.constants.AppConstants;
import com.michaelfotiadis.deskalarm.constants.AppConstants.PreferenceKeys;
import com.michaelfotiadis.deskalarm.constants.Singleton;
import com.michaelfotiadis.deskalarm.containers.ErgoTimeDataInstance;
import com.michaelfotiadis.deskalarm.utils.AppUtils;
import com.michaelfotiadis.deskalarm.utils.FileUtils;
import com.michaelfotiadis.deskalarm.utils.Logger;

import java.util.Calendar;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

public class ErgoDataManager {

    private final String TAG = "UsageDataLoader";

    // minimum value required to store to memory
    private final long MINIMUM_INTERVAL = 1;

    private final Context mContext;
    private ErgoTimeDataInstance mDataInstance;

    public ErgoDataManager(final Context context) {
        mContext = context;
    }


    private void broadcastNotificationDataChanged() {
        final Intent broadcastIntent = new Intent(AppConstants.Broadcasts.DATA_CHANGED.getString());
        mContext.sendBroadcast(broadcastIntent);
    }

    /**
     * Retrieve in memory data but only for the current day
     *
     * @return
     */
    public SortedMap<String, ErgoTimeDataInstance> retrieveDailyData() {
        SortedMap<String, ErgoTimeDataInstance> filteredData = new TreeMap<String, ErgoTimeDataInstance>();
        // get the current day
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        if (filteredData != null) {
            // iterate through the entry set
            for (Entry<String, ErgoTimeDataInstance> entry : Singleton.getInstance().getUsageData().entrySet()) {
                if (entry.getValue().getCalendarLogged().get(Calendar.DAY_OF_MONTH) == day) {
                    filteredData.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return filteredData;
    }

    /**
     * Clears data stored in memory
     */
    public void clearUserData() {
        Singleton.getInstance().getUsageData().clear();
    }

    /**
     * Stores data
     *
     * @param mContext
     */
    public void storeIdleData() {
        // get time started from preferences
        final long storedTimeStarted = new AppUtils().getAppSharedPreferences(
                mContext.getApplicationContext()).getLong(PreferenceKeys.KEY_1.getString(), 0);

        // store data in an object
        mDataInstance = new ErgoTimeDataInstance(storedTimeStarted);

        if (storedTimeStarted != 0 && mDataInstance.getTimeLogged() >= MINIMUM_INTERVAL) {
            // store the data in minutes (the object is handling the logic)
            if (Singleton.getInstance().addToUsageData(mContext.getApplicationContext(), mDataInstance)) {
                new FileUtils().writeToSettingsFile(mContext, mDataInstance.toOutputString());
            } else {
                Logger.i(TAG, "Data not logged");
            }
        }
        // notify listeners that the data has changed
        broadcastNotificationDataChanged();
    }
}
