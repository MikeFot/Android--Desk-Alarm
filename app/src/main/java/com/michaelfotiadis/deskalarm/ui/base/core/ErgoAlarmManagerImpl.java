package com.michaelfotiadis.deskalarm.ui.base.core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.michaelfotiadis.deskalarm.constants.AppConstants;
import com.michaelfotiadis.deskalarm.model.Broadcasts;
import com.michaelfotiadis.deskalarm.model.Payloads;
import com.michaelfotiadis.deskalarm.model.Requests;
import com.michaelfotiadis.deskalarm.services.AlarmService;
import com.michaelfotiadis.deskalarm.ui.base.core.preference.PreferenceHandler;
import com.michaelfotiadis.deskalarm.ui.base.core.preference.PreferenceHandlerImpl;
import com.michaelfotiadis.deskalarm.utils.PrimitiveConversions;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

import java.util.concurrent.TimeUnit;

public final class ErgoAlarmManagerImpl implements ErgoAlarmManager {

    private final Context mContext;
    private final PreferenceHandler mPreferenceHandler;
    private final ErgoDataManagerImpl mDataManager;
    private final AlarmManager mAlarmManager;

    /*package*/ ErgoAlarmManagerImpl(final Context context) {
        this.mContext = context;
        this.mPreferenceHandler = new PreferenceHandlerImpl(context);
        this.mDataManager = new ErgoDataManagerImpl(context);
        this.mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public void setAlarm(final ALARM_MODE mode) {
        final Long interval;

        switch (mode) {
            case NORMAL:
                // schedule the alarm
                interval = mPreferenceHandler.getLongPreference(PreferenceHandlerImpl.PreferenceKey.ALARM_INTERVAL);
                mPreferenceHandler.writeLongPreference(PreferenceHandlerImpl.PreferenceKey.TIME_STARTED, System.currentTimeMillis());
                mDataManager.storeIdleData();
                break;
            case SNOOZE:
                interval = mPreferenceHandler.getLongPreference(PreferenceHandlerImpl.PreferenceKey.SNOOZE_INTERVAL);
                break;
            case REPEAT:
                interval = mPreferenceHandler.getLongPreference(PreferenceHandlerImpl.PreferenceKey.ALARM_INTERVAL);
                mDataManager.storeIdleData();
                mPreferenceHandler.writeLongPreference(PreferenceHandlerImpl.PreferenceKey.TIME_STARTED, System.currentTimeMillis());
                break;
            case AUTO:
                interval = mPreferenceHandler.getLongPreference(PreferenceHandlerImpl.PreferenceKey.ALARM_INTERVAL);
                mDataManager.storeIdleData();
                mPreferenceHandler.writeLongPreference(PreferenceHandlerImpl.PreferenceKey.TIME_STARTED, System.currentTimeMillis());
                break;
            case STOPPED:
                interval = mPreferenceHandler.getLongPreference(PreferenceHandlerImpl.PreferenceKey.ALARM_INTERVAL);
                break;
            default:
                interval = mPreferenceHandler.getLongPreference(PreferenceHandlerImpl.PreferenceKey.ALARM_INTERVAL);
                break;
        }


        if (interval != null && interval > 0) {
            final long targetTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(interval);
            //***
            AppLog.d(String.format("\n\n***\nAlarm is set %s\n***\n",
                    PrimitiveConversions.getDate(targetTime, AppConstants.SIMPLE_DATE_FORMAT_STRING)));

            AppLog.d(String.format("Current time: %s",
                    PrimitiveConversions.getDate(System.currentTimeMillis(), AppConstants.SIMPLE_DATE_FORMAT_STRING)));
            AppLog.d(String.format("Alarm time: %s",
                    PrimitiveConversions.getDate(targetTime, AppConstants.SIMPLE_DATE_FORMAT_STRING)));
            //***

            final Intent intent = new Intent(mContext, AlarmService.class);
            intent.putExtra(Payloads.ALARM_PAYLOAD.getString(), Requests.REQUEST_CODE_1.getCode());
            final PendingIntent operation = PendingIntent.getService(mContext, 0, intent, 0);
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, targetTime, operation);

            // send a broadcast
            broadcastAlarmMode(mode);

            AppLog.i("Scheduling Next Alarm");
        } else {
            this.cancelAlarm();
            broadcastAlarmMode(ALARM_MODE.STOPPED);
        }
    }

    /**
     * Stops a predefined alarm
     */
    @Override
    public void cancelAlarm() {
        mDataManager.storeIdleData();

        mPreferenceHandler.writeLongPreference(PreferenceHandlerImpl.PreferenceKey.TIME_STARTED, 0L);

        final AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        final Intent intent = new Intent(mContext, AlarmService.class);

        // creating a Pending Intent
        final PendingIntent operation = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // cancel the alarm
        alarmManager.cancel(operation);
        AppLog.i("Stopped Alarm");
    }

    /**
     * Save time started to shared preferences and schedule alarm if it is greater than 0
     *
     * @param timeStarted
     */
    @Override
    public void saveTimeToPreferencesAndStore(final long timeStarted) {
        mPreferenceHandler.writeLongPreference(PreferenceHandlerImpl.PreferenceKey.TIME_STARTED, timeStarted);
        if (timeStarted > 0) {
            // TODO do we still need this?
            // store the time
        }
    }

    private void broadcastAlarmMode(final ALARM_MODE mode) {
        final Intent broadcastIntent = new Intent(Broadcasts.CLOCK_MODE_CHANGED.getString());
        broadcastIntent.putExtra(Payloads.CLOCK_MODE_PAYLOAD.getString(), mode);
        mContext.sendBroadcast(broadcastIntent);
    }

}
