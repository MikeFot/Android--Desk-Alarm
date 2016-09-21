package com.michaelfotiadis.deskalarm.dialogs;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.widget.TimePicker;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

/**
 * Class extending DialogFragment. Stores Alarm Time to SharedPreferences.
 *
 * @author Michael Fotiadis
 */
public class TimePickerDialogWrapper implements TimePickerDialog.OnTimeSetListener {

    private final TimePickerDialog mDialog;
    private final int mOriginalSetting;
    private final SharedPreferences mSharedPreferences;

    public static TimePickerDialogWrapper newInstance(final Activity activity, final SharedPreferences sharedPreferences) {
        return new TimePickerDialogWrapper(activity, sharedPreferences);
    }

    public TimePickerDialogWrapper(final Activity activity, final SharedPreferences sharedPreferences) {

        mSharedPreferences = sharedPreferences;

        mOriginalSetting = mSharedPreferences.getInt(activity.getString(R.string.pref_alarm_interval_key),
                activity.getResources().getInteger(R.integer.time_to_alarm));

        final int hour = mOriginalSetting / 60;
        int minute = mOriginalSetting % 60;

        // sanitise display
        if (minute <= 0) {
            minute = 0;
        }

        // get a 24 hour time picker
        mDialog = new TimePickerDialog(activity, this, hour, minute, true);

        mDialog.setTitle(activity.getString(R.string.dialog_set_interval_between_alarms));

    }

    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    @Override
    public void onTimeSet(final TimePicker view, final int hourOfDay, final int minute) {
        AppLog.i("Value selected " + hourOfDay + " : " + minute);

        int timeToAlarm = hourOfDay * 60 + minute;

        if (timeToAlarm == mOriginalSetting) {
            mDialog.dismiss();
            return;
        }

        // sanitise time
        if (timeToAlarm <= 0) {
            timeToAlarm = 1;
        }

        // ensure that the edit will persist by creating an Editor instance
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(mDialog.getContext().getString(R.string.pref_alarm_interval_key), timeToAlarm);
        editor.apply();
    }

}