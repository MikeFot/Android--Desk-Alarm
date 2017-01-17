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
public final class TimePickerDialogWrapper {

    private static final int MAX_TIME = 120;

    private final AlertDialog mDialog;
    private final PreferenceHandler mPreferenceHandler;
    private final PreferenceHandler.PreferenceKey mKey;

    public static TimePickerDialogWrapper newAlarmInstance(final Activity activity,
                                                           final PreferenceHandler preferenceHandler) {
        return new TimePickerDialogWrapper(activity, preferenceHandler, PreferenceHandler.PreferenceKey.ALARM_INTERVAL);
    }

    public static TimePickerDialogWrapper newSnoozeInstance(final Activity activity,
                                                            final PreferenceHandler preferenceHandler) {
        return new TimePickerDialogWrapper(activity, preferenceHandler, PreferenceHandler.PreferenceKey.SNOOZE_INTERVAL);
    }

    private TimePickerDialogWrapper(final Activity activity,
                                    final PreferenceHandler preferenceHandler,
                                    final PreferenceHandler.PreferenceKey key) {

        mKey = key;
        mPreferenceHandler = preferenceHandler;

        final int preferencesTime = mPreferenceHandler.getInt(mKey);

        final int originalSetting;

        if (preferencesTime > MAX_TIME) {
            originalSetting = MAX_TIME;
        } else {
            originalSetting = preferencesTime;
        }

        int minute = originalSetting % 60;

        // sanitise display
        if (minute <= 0) {
            minute = 0;
        }

        final NumberPicker picker = new NumberPicker(activity);
        picker.setMaxValue(MAX_TIME);




        // get a 24 hour time picker
        mDialog = initNumberPicker(activity, minute, MAX_TIME);

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

        final String title;
        switch (mKey) {

            case ALARM_INTERVAL:
                title = context.getString(R.string.dialog_set_interval_between_alarms);
                break;
            case SNOOZE_INTERVAL:
                title = context.getString(R.string.dialog_set_snooze_time);
                break;
            default:
                title = context.getString(R.string.dialog_set_interval_between_alarms);
        }
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setView(linearLayout);
        //noinspection AnonymousInnerClassMayBeStatic
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                onTimeSet(numberPicker.getValue());
                                dialog.dismiss();
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

    @SuppressWarnings("unused")
    public void dismiss() {
        mDialog.dismiss();
    }

    private void onTimeSet(final int minutes) {
        int timeToAlarm = minutes;

        // sanitise time
        if (timeToAlarm <= 0) {
            timeToAlarm = 1;
        }

        AppLog.d("Time set= " + timeToAlarm);

        mPreferenceHandler.writeInt(mKey, timeToAlarm);

    }

}