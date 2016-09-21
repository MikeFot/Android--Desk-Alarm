package com.michaelfotiadis.deskalarm.common.base.core;

import android.content.Context;
import android.content.Intent;

import com.michaelfotiadis.deskalarm.constants.Singleton;
import com.michaelfotiadis.deskalarm.containers.ErgoTimeDataInstance;
import com.michaelfotiadis.deskalarm.model.Broadcasts;
import com.michaelfotiadis.deskalarm.model.PreferenceKeys;
import com.michaelfotiadis.deskalarm.utils.FileHelper;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

import java.util.Calendar;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

public class ErgoDataManager {

    // minimum value required to store to memory
    private final long MINIMUM_INTERVAL = 1;

    private final Context mContext;
    private final PreferenceHandler mPreferenceHandler;
    private final FileHelper mFileHelper;
    private ErgoTimeDataInstance mDataInstance;

    protected ErgoDataManager(final Context context) {
        this(context, new PreferenceHandler(context), new FileHelper(context));
    }

    protected ErgoDataManager(final Context context, final PreferenceHandler preferenceHandler, final FileHelper fileHelper) {
        this.mContext = context;
        this.mPreferenceHandler = preferenceHandler;
        this.mFileHelper = fileHelper;
    }

    /**
     * Retrieve in memory data but only for the current day
     *
     * @return
     */
    public SortedMap<String, ErgoTimeDataInstance> retrieveDailyData() {
        final SortedMap<String, ErgoTimeDataInstance> filteredData = new TreeMap<String, ErgoTimeDataInstance>();
        // get the current day
        final int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        // iterate through the entry set
        for (final Entry<String, ErgoTimeDataInstance> entry : Singleton.getInstance().getUsageData().entrySet()) {
            if (entry.getValue().getCalendarLogged().get(Calendar.DAY_OF_MONTH) == day) {
                filteredData.put(entry.getKey(), entry.getValue());
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
     */
    public void storeIdleData() {
        // get time started from preferences
        final long storedTimeStarted = mPreferenceHandler.getAppSharedPreferences().getLong(PreferenceKeys.KEY_1.getString(), 0);

        // store data in an object
        mDataInstance = new ErgoTimeDataInstance(storedTimeStarted);

        if (storedTimeStarted != 0 && mDataInstance.getTimeLogged() >= MINIMUM_INTERVAL) {
            // store the data in minutes (the object is handling the logic)
            if (Singleton.getInstance().addToUsageData(mContext.getApplicationContext(), mDataInstance)) {
                mFileHelper.writeToSettingsFile(mDataInstance.toOutputString());
            } else {
                AppLog.i("Data not logged");
            }
        }
        // notify listeners that the data has changed
        broadcastNotificationDataChanged();
    }

    private void broadcastNotificationDataChanged() {
        final Intent broadcastIntent = new Intent(Broadcasts.DATA_CHANGED.getString());
        mContext.sendBroadcast(broadcastIntent);
    }
}
