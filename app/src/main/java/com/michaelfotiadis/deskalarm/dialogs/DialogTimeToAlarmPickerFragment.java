package com.michaelfotiadis.deskalarm.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TimePicker;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.utils.AppUtils;
import com.michaelfotiadis.deskalarm.utils.Logger;

/**
 * Class extending DialogFragment. Stores Alarm Time to SharedPreferences.
 *
 * @author Michael Fotiadis
 */
public class DialogTimeToAlarmPickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private final String TAG = "TimePickerFragment";

    private int mOriginalSetting;

    private SharedPreferences mSharedPreferences;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mSharedPreferences = new AppUtils().getAppSharedPreferences(getActivity());

        // Use the current time as the default values for the picker
        mOriginalSetting = mSharedPreferences.getInt(getString(R.string.pref_alarm_interval_key),
                getResources().getInteger(R.integer.time_to_alarm));

        final int hour = mOriginalSetting / 60;
        int minute = mOriginalSetting % 60;

        // sanitise display
        if (minute <= 0) {
            minute = 0;
        }

        // get a 24 hour time picker
        final TimePickerDialog dialog = new TimePickerDialog(getActivity(), this, hour, minute,
                true);

        dialog.setTitle(getActivity().getString(R.string.dialog_set_interval_between_alarms));
        dialog.setCancelable(false);

        setRetainInstance(true);

        // return the new instance of the dialog
        return dialog;
    }

    @Override
    public void onDestroyView() {
        if (getDialog() != null && getRetainInstance())
            getDialog().setDismissMessage(null);
        super.onDestroyView();
    }

    @Override
    public void onTimeSet(final TimePicker view, final int hourOfDay, final int minute) {
        Logger.i(TAG, "Value selected " + hourOfDay + " : " + minute);

        int timeToAlarm = hourOfDay * 60 + minute;

        if (timeToAlarm == mOriginalSetting) {
            this.dismiss();
            return;
        }

        // sanitise time
        if (timeToAlarm <= 0) {
            timeToAlarm = 1;
        }

        // ensure that the edit will persist by creating an Editor instance
        final SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putInt(getActivity().getString(R.string.pref_alarm_interval_key), timeToAlarm);
        editor.commit();
    }

}