package com.michaelfotiadis.deskalarm.ui.base.core;

import android.content.Context;
import android.content.Intent;

import com.michaelfotiadis.deskalarm.constants.DataStorage;
import com.michaelfotiadis.deskalarm.containers.TimeModelInstance;
import com.michaelfotiadis.deskalarm.model.Broadcasts;
import com.michaelfotiadis.deskalarm.ui.base.core.preference.PreferenceHandler;
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
    private TimeModelInstance mDataInstance;

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
    public SortedMap<String, TimeModelInstance> retrieveDailyData() {
        final SortedMap<String, TimeModelInstance> filteredData = new TreeMap<String, TimeModelInstance>();
        // get the current day
        final int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        // iterate through the entry set
        for (final Entry<String, TimeModelInstance> entry : DataStorage.getInstance().getUsageData().entrySet()) {
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
        DataStorage.getInstance().getUsageData().clear();
    }

    /**
     * Stores data
     */
    public void storeIdleData() {
        // get time started from preferences
        final long storedTimeStarted = mPreferenceHandler.getLongPreference(PreferenceHandler.PreferenceKey.TIME_STARTED);

        // store data in an object
        mDataInstance = new TimeModelInstance(storedTimeStarted);

        if (storedTimeStarted != 0 && mDataInstance.getTimeLogged() >= MINIMUM_INTERVAL) {
            // store the data in minutes (the object is handling the logic)
            if (DataStorage.getInstance().addToUsageData(mContext.getApplicationContext(), mDataInstance)) {
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
