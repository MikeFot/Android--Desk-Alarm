package com.michaelfotiadis.deskalarm.ui.base.core;

import android.content.Context;
import android.content.Intent;

import com.michaelfotiadis.deskalarm.constants.DataStorage;
import com.michaelfotiadis.deskalarm.containers.TimeModel;
import com.michaelfotiadis.deskalarm.model.Broadcasts;
import com.michaelfotiadis.deskalarm.ui.base.core.preference.PreferenceHandler;
import com.michaelfotiadis.deskalarm.ui.base.core.preference.PreferenceHandlerImpl;
import com.michaelfotiadis.deskalarm.utils.FileHelper;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

import java.util.Calendar;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

public class DataManagerImpl implements DataManager {

    // minimum value required to store to memory
    private static final long MINIMUM_INTERVAL = 1;

    private final Context mContext;
    private final PreferenceHandler mPreferenceHandler;
    private final FileHelper mFileHelper;

    /*package*/ DataManagerImpl(final Context context) {
        this(context, new PreferenceHandlerImpl(context), new FileHelper(context));
    }

    /*package*/ DataManagerImpl(final Context context, final PreferenceHandler preferenceHandler, final FileHelper fileHelper) {
        this.mContext = context;
        this.mPreferenceHandler = preferenceHandler;
        this.mFileHelper = fileHelper;
    }

    /**
     * Retrieve in memory data but only for the current day
     *
     * @return Sorted map String-TimeModel
     */
    @Override
    @SuppressWarnings("MethodMayBeStatic")
    public SortedMap<String, TimeModel> retrieveDailyData() {
        final SortedMap<String, TimeModel> filteredData = new TreeMap<String, TimeModel>();
        // get the current day
        final int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        // iterate through the entry set
        for (final Entry<String, TimeModel> entry : DataStorage.getInstance().getUsageData().entrySet()) {
            if (entry.getValue().getCalendarLogged().get(Calendar.DAY_OF_MONTH) == day) {
                filteredData.put(entry.getKey(), entry.getValue());
            }
        }
        return filteredData;
    }

    /**
     * Clears data stored in memory
     */
    @Override
    @SuppressWarnings("MethodMayBeStatic")
    public void clearUserData() {
        DataStorage.getInstance().getUsageData().clear();
    }

    /**
     * Stores data
     */
    /*package*/ void storeIdleData() {
        // get time started from prefs
        final long storedTimeStarted = mPreferenceHandler.getLong(PreferenceHandlerImpl.PreferenceKey.TIME_STARTED);

        // store data in an object
        final TimeModel dataInstance = new TimeModel(storedTimeStarted);

        if (storedTimeStarted != 0 && dataInstance.getTimeLogged() >= MINIMUM_INTERVAL) {
            // store the data in minutes (the object is handling the logic)
            if (DataStorage.getInstance().addToUsageData(mContext.getApplicationContext(), dataInstance)) {
                mFileHelper.writeToSettingsFile(dataInstance.toOutputString());
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
