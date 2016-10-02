package com.michaelfotiadis.deskalarm.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;

import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.ui.base.core.preference.PreferenceHandler;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

/**
 * Class extending DialogFragment. Stores Alarm Time to SharedPreferences.
 *
 * @author Michael Fotiadis
 */
public class TimePickerDialogWrapper {

    private static final int MAX_TIME = 120;

    private final AlertDialog mDialog;
    private final int mOriginalSetting;
    private final PreferenceHandler mPreferenceHandler;

    public static TimePickerDialogWrapper newInstance(final Activity activity, final PreferenceHandler preferenceHandler) {
        return new TimePickerDialogWrapper(activity, preferenceHandler);
    }

    public TimePickerDialogWrapper(final Activity activity, final PreferenceHandler preferenceHandler) {

        mPreferenceHandler = preferenceHandler;

        final int preferencesTime = mPreferenceHandler.getInt(PreferenceHandler.PreferenceKey.ALARM_INTERVAL);


        if (preferencesTime > MAX_TIME) {
            mOriginalSetting = MAX_TIME;
        } else {
            mOriginalSetting = (int) preferencesTime;
        }

        final int hour = mOriginalSetting / 60;
        int minute = mOriginalSetting % 60;

        // sanitise display
        if (minute <= 0) {
            minute = 0;
        }

        final NumberPicker picker = new NumberPicker(activity);
        picker.setMaxValue(MAX_TIME);




        // get a 24 hour time picker
        mDialog = initNumberPicker(activity, mOriginalSetting, MAX_TIME);

    }

    private AlertDialog initNumberPicker(final Context context, final int currentValue, final int maxValue) {
        final RelativeLayout linearLayout = new RelativeLayout(context);
        final NumberPicker numberPicker = new NumberPicker(context);
        numberPicker.setMaxValue(maxValue);
        numberPicker.setMinValue(1);
        numberPicker.setValue(currentValue);
        numberPicker.setBackgroundColor(ContextCompat.getColor(context, R.color.md_grey_100));

        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
        final RelativeLayout.LayoutParams numberPickerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(4, 4, 4, 4);
        numberPickerParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        linearLayout.setLayoutParams(params);
        linearLayout.addView(numberPicker, numberPickerParams);


        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(context.getString(R.string.dialog_set_interval_between_alarms));
        alertDialogBuilder.setView(linearLayout);
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                onTimeSet(numberPicker.getValue());
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog,
                                                final int id) {
                                dialog.cancel();
                            }
                        });

        return alertDialogBuilder.create();
    }

    public void show() {
        mDialog.show();
    }

    public void dismiss() {
        mDialog.dismiss();
    }

    private void onTimeSet(final int minutes) {
        int timeToAlarm = minutes;

        AppLog.d("Time to alarm= " + timeToAlarm);

        if (timeToAlarm == mOriginalSetting) {
            mDialog.dismiss();
            return;
        }

        // sanitise time
        if (timeToAlarm <= 0) {
            timeToAlarm = 1;
        }
        mPreferenceHandler.writeInt(PreferenceHandler.PreferenceKey.ALARM_INTERVAL, timeToAlarm);
    }

}