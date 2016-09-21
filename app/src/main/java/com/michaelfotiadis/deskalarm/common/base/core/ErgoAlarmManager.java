package com.michaelfotiadis.deskalarm.common.base.core;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.constants.AppConstants;
import com.michaelfotiadis.deskalarm.model.Broadcasts;
import com.michaelfotiadis.deskalarm.model.Payloads;
import com.michaelfotiadis.deskalarm.model.PreferenceKeys;
import com.michaelfotiadis.deskalarm.model.Requests;
import com.michaelfotiadis.deskalarm.services.ErgoAlarmService;
import com.michaelfotiadis.deskalarm.utils.PrimitiveConversions;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

import java.util.Calendar;

public final class ErgoAlarmManager {

    private final Context mContext;
    private final PreferenceHandler mPreferenceHandler;
    private final ErgoDataManager mDataManager;
    private final AlarmManager mAlarmManager;

    protected ErgoAlarmManager(final Context context) {
        this.mContext = context;
        this.mPreferenceHandler = new PreferenceHandler(context);
        this.mDataManager = new ErgoDataManager(context);
        this.mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    public void setAlarm(final ALARM_MODE mode) {
        final int interval;
        if (mode == ALARM_MODE.NORMAL) {
            // schedule the alarm
            interval = mPreferenceHandler.getAppSharedPreferences().getInt(mContext.getString(R.string.pref_alarm_interval_key), 1);
            final Editor editor = mPreferenceHandler.getAppSharedPreferences().edit();
            editor.putLong(PreferenceKeys.KEY_1.getString(), Calendar.getInstance().getTimeInMillis());
            editor.apply();
            mDataManager.storeIdleData();
        } else if (mode == ALARM_MODE.SNOOZE) {
            interval = mPreferenceHandler.getAppSharedPreferences().getInt(mContext.getString(R.string.pref_snooze_interval_key), 1);
        } else if (mode == ALARM_MODE.REPEAT || mode == ALARM_MODE.AUTO) {
            interval = mPreferenceHandler.getAppSharedPreferences().getInt(mContext.getString(R.string.pref_alarm_interval_key), 1);
            mDataManager.storeIdleData();
            final Editor editor = mPreferenceHandler.getAppSharedPreferences().edit();
            editor.putLong(PreferenceKeys.KEY_1.getString(), Calendar.getInstance().getTimeInMillis());
            editor.apply();
        } else {
            interval = mPreferenceHandler.getAppSharedPreferences().getInt(mContext.getString(R.string.pref_alarm_interval_key), 1);
        }

        if (interval > 0) {
            final long targetTime = Calendar.getInstance().getTimeInMillis() + (interval *
                    AppConstants.FACTOR_MSEC_TO_MINUTES);
            //***
            AppLog.d(String.format("\n\n***\nAlarm is set %s\n***\n",
                    PrimitiveConversions.getDate(targetTime, AppConstants.SIMPLE_DATE_FORMAT_STRING)));

            AppLog.d(String.format("Current time: %s",
                    PrimitiveConversions.getDate(Calendar.getInstance().getTimeInMillis(), AppConstants.SIMPLE_DATE_FORMAT_STRING)));
            AppLog.d(String.format("Alarm time: %s",
                    PrimitiveConversions.getDate(targetTime, AppConstants.SIMPLE_DATE_FORMAT_STRING)));
            //***


            final Intent intent = new Intent(mContext, ErgoAlarmService.class);
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
    public void cancelAlarm() {
        mDataManager.storeIdleData();

        final Editor editor = mPreferenceHandler.getAppSharedPreferences().edit();
        editor.putLong(PreferenceKeys.KEY_1.getString(), 0);
        editor.apply();

        final AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        final Intent intent = new Intent(mContext, ErgoAlarmService.class);

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
    public void saveTimeToPreferencesAndStore(final long timeStarted) {

        final Editor editor = mPreferenceHandler.getAppSharedPreferences().edit();
        editor.putLong(PreferenceKeys.KEY_1.getString(), timeStarted);
        editor.apply();

        if (timeStarted > 0) {
            // store the time

        }
    }

    private void broadcastAlarmMode(final ALARM_MODE mode) {
        final Intent broadcastIntent = new Intent(Broadcasts.CLOCK_MODE_CHANGED.getString());
        broadcastIntent.putExtra(Payloads.CLOCK_MODE_PAYLOAD.getString(), mode);
        mContext.sendBroadcast(broadcastIntent);
    }

    public enum ALARM_MODE {
        NORMAL, SNOOZE, REPEAT, STOPPED, AUTO
    }

}
