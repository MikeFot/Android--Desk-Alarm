package com.michaelfotiadis.deskalarm.dialogs;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.widget.TimePicker;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

/**
 * Class extending DialogFragment. Stores Snooze Time to SharedPreferences.
 *
 * @author Michael Fotiadis
 */
public class TimeToSnoozeDialogWrapper implements TimePickerDialog.OnTimeSetListener {

    private final TimePickerDialog mDialog;
    private final SharedPreferences mSharedPreferences;

    private final int mOriginalSetting;

    public static TimeToSnoozeDialogWrapper newInstance(final Activity activity, final SharedPreferences sharedPreferences) {
        return new TimeToSnoozeDialogWrapper(activity, sharedPreferences);
    }

    public TimeToSnoozeDialogWrapper(final Activity activity, final SharedPreferences sharedPreferences) {

        mSharedPreferences = sharedPreferences;


        // Use the current time as the default values for the picker
        mOriginalSetting = mSharedPreferences.getInt(activity.getString(R.string.pref_snooze_interval_key), activity.getResources().getInteger(R.integer.time_to_snooze));

        final int hour = mOriginalSetting / 60;
        int minute = mOriginalSetting % 60;

        // sanitise display
        if (minute <= 0) {
            minute = 0;
        }

        mDialog = new TimePickerDialog(activity, this, hour, minute, true);

        mDialog.setTitle(activity.getString(R.string.dialog_set_snooze_time));
        mDialog.setCancelable(true);

    }

    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    @Override
    public void onTimeSet(final TimePicker view, final int hourOfDay, final int minute) {
        AppLog.d("Value selected " + hourOfDay + " : " + minute);

        int timeToAlarm = hourOfDay * 60 + minute;

        if (timeToAlarm == mOriginalSetting) {
            dismiss();
            return;
        }

        // sanitise time
        if (timeToAlarm <= 0) {
            timeToAlarm = 1;
        }

        // ensure that the edit will persist by creating an Editor instance
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(mDialog.getContext().getString(R.string.pref_snooze_interval_key), timeToAlarm);
        editor.apply();
    }
}