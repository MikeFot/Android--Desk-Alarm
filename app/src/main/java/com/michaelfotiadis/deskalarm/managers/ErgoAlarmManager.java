package com.michaelfotiadis.deskalarm.managers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.constants.AppConstants;
import com.michaelfotiadis.deskalarm.constants.AppConstants.PreferenceKeys;
import com.michaelfotiadis.deskalarm.services.ErgoAlarmService;
import com.michaelfotiadis.deskalarm.utils.AppUtils;
import com.michaelfotiadis.deskalarm.utils.Logger;
import com.michaelfotiadis.deskalarm.utils.PrimitiveConversions;

import java.util.Calendar;

public final class ErgoAlarmManager {

    private final String TAG = "Alarm Manager";


    public enum ALARM_MODE {
        NORMAL, SNOOZE, REPEAT, STOPPED, AUTO
    }

    /**
     * Method which sets an alarm after a fixed interval
     *
     * @param milliSeconds Milliseconds long time to next alarm
     */
    public void setAlarm(final Context context, final ALARM_MODE mode) {
        final int interval;
        if (mode == ALARM_MODE.NORMAL) {
            // schedule the alarm
            interval = new AppUtils().getAppSharedPreferences(context).
                    getInt(context.getString(R.string.pref_alarm_interval_key), 1);
            final Editor editor = new AppUtils().getAppSharedPreferences(context).edit();
            editor.putLong(PreferenceKeys.KEY_1.getString(), Calendar.getInstance().getTimeInMillis());
            editor.commit();
            new ErgoDataManager(context).storeIdleData();
        } else if (mode == ALARM_MODE.SNOOZE) {
            interval = new AppUtils().getAppSharedPreferences(context).
                    getInt(context.getString(R.string.pref_snooze_interval_key), 1);
        } else if (mode == ALARM_MODE.REPEAT || mode == ALARM_MODE.AUTO) {
            interval = new AppUtils().getAppSharedPreferences(context).
                    getInt(context.getString(R.string.pref_alarm_interval_key), 1);
            new ErgoDataManager(context).storeIdleData();
            final Editor editor = new AppUtils().getAppSharedPreferences(context).edit();
            editor.putLong(PreferenceKeys.KEY_1.getString(), Calendar.getInstance().getTimeInMillis());
            editor.commit();
        } else {
            interval = new AppUtils().getAppSharedPreferences(context).
                    getInt(context.getString(R.string.pref_alarm_interval_key), 1);
        }

        if (interval > 0) {
            final long targetTime = Calendar.getInstance().getTimeInMillis() + (interval *
                    AppConstants.FACTOR_MSEC_TO_MINUTES);
            //***
            Logger.d(TAG, "\n\n***\n" + "Alarm is set " +
                    PrimitiveConversions.getDate(targetTime, AppConstants.SIMPLE_DATE_FORMAT_STRING) +
                    "\n" + "***\n");

            Logger.d(TAG, "Current time: " + PrimitiveConversions.getDate(
                    Calendar.getInstance().getTimeInMillis(), AppConstants.SIMPLE_DATE_FORMAT_STRING));
            Logger.d(TAG, "Alarm time: " + PrimitiveConversions.getDate(
                    targetTime, AppConstants.SIMPLE_DATE_FORMAT_STRING));
            //***

            final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            final Intent intent = new Intent(context, ErgoAlarmService.class);
            intent.putExtra(AppConstants.Payloads.ALARM_PAYLOAD.getString(),
                    AppConstants.Requests.REQUEST_CODE_1.getCode());
            final PendingIntent operation = PendingIntent.getService(context, 0, intent, 0);
            alarmManager.set(AlarmManager.RTC_WAKEUP, targetTime, operation);

            // send a broadcast
            broadcastAlarmMode(context, mode);

            Logger.i(TAG, "Scheduling Next Alarm");
        } else {
            this.cancelAlarm(context);
            broadcastAlarmMode(context, ALARM_MODE.STOPPED);
        }
    }

    /**
     * Stops a predefined alarm
     */
    public void cancelAlarm(final Context context) {
        new ErgoDataManager(context).storeIdleData();

        final Editor editor = new AppUtils().getAppSharedPreferences(context).edit();
        editor.putLong(PreferenceKeys.KEY_1.getString(), 0);
        editor.commit();

        final AlarmManager alarmManager = (AlarmManager) context.
                getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        final Intent intent = new Intent(context, ErgoAlarmService.class);

        // creating a Pending Intent
        final PendingIntent operation = PendingIntent.
                getActivity(context, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);

        // cancel the alarm
        alarmManager.cancel(operation);
        Logger.i(TAG, "Stopped Alarm");
    }

    /**
     * Save time started to shared preferences and schedule alarm if it is greater than 0
     *
     * @param timeStarted
     */
    public void saveTimeToPreferencesAndStore(final long timeStarted, final Context context) {

        final Editor editor = new AppUtils().getAppSharedPreferences(context).edit();
        editor.putLong(PreferenceKeys.KEY_1.getString(), timeStarted);
        editor.commit();

        if (timeStarted > 0) {
            // store the time

        }
    }

    private void broadcastAlarmMode(final Context context, final ALARM_MODE mode) {
        final Intent broadcastIntent = new Intent(AppConstants.Broadcasts.CLOCK_MODE_CHANGED.getString());
        broadcastIntent.putExtra(AppConstants.Payloads.CLOCK_MODE_PAYLOAD.getString(), mode);
        context.sendBroadcast(broadcastIntent);
    }

}
