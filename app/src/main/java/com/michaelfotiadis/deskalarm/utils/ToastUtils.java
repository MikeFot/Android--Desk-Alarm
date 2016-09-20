package com.michaelfotiadis.deskalarm.utils;

import android.app.Activity;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.michaelfotiadis.deskalarm.R;
import com.michaelfotiadis.deskalarm.managers.ErgoAlarmManager;

public class ToastUtils {

    private static final String TAG = "TOAST UTILITIES";

    private SuperActivityToast mSuperActivityToast;

    /**
     * Method for dismissing all active toasts
     */
    public void dismissActiveToasts() {
        Logger.d(TAG, "Dismissing Toasts");
        if (mSuperActivityToast != null) {
            SuperActivityToast.cancelAllSuperToasts();
        }
    }

    /**
     * Method for creating and showing an info toast
     *
     * @param activity
     * @param message
     * @return
     */
    public SuperActivityToast makeInfoToast(final Activity activity, final String message) {
        dismissActiveToasts();
        Logger.d(TAG, "Making Info Toast");

        mSuperActivityToast = new SuperActivityToast(activity, Style.TYPE_STANDARD);

        mSuperActivityToast.setAnimations(Style.ANIMATIONS_FADE);
        mSuperActivityToast.setDuration(Style.DURATION_SHORT);
        mSuperActivityToast.setColor(Style.green().color);
        mSuperActivityToast.setText(message);
        mSuperActivityToast.setTextSize(Style.TEXTSIZE_MEDIUM);
        mSuperActivityToast.setTouchToDismiss(true);
        mSuperActivityToast.show();
        return mSuperActivityToast;
    }

    /**
     * Method for creating and showing an info toast
     *
     * @param activity
     * @param message
     * @return
     */
    public SuperActivityToast makeInfoToast(final Activity activity, final String message, final int color) {
        dismissActiveToasts();
        Logger.d(TAG, "Making Info Toast");

        mSuperActivityToast = new SuperActivityToast(activity, Style.TYPE_STANDARD);

        mSuperActivityToast.setAnimations(Style.ANIMATIONS_FADE);
        mSuperActivityToast.setDuration(Style.DURATION_SHORT);
        mSuperActivityToast.setColor(color);
        mSuperActivityToast.setText(message);
        mSuperActivityToast.setTextSize(Style.TEXTSIZE_MEDIUM);
        mSuperActivityToast.setTouchToDismiss(true);
        mSuperActivityToast.show();
        return mSuperActivityToast;
    }

    /**
     * Method for creating and showing a progress toast
     *
     * @param activity
     * @param message
     * @return
     */
    public SuperActivityToast makeProgressToast(final Activity activity, final String message) {
        dismissActiveToasts();
        Logger.d(TAG, "Making Progress Toast");

        mSuperActivityToast = new SuperActivityToast(activity, Style.TYPE_PROGRESS_CIRCLE);

        mSuperActivityToast.setAnimations(Style.ANIMATIONS_FADE);
        mSuperActivityToast.setDuration(Style.DURATION_LONG);
        mSuperActivityToast.setColor(Style.blueGrey().color);
        mSuperActivityToast.setText(message);
        mSuperActivityToast.setTextSize(Style.TEXTSIZE_MEDIUM);
        mSuperActivityToast.setIndeterminate(true);

        mSuperActivityToast.show();
        return mSuperActivityToast;
    }

    /**
     * Method for creating and showing a warning toast
     *
     * @param activity
     * @param message
     * @return
     */
    public SuperActivityToast makeWarningToast(final Activity activity, final String message) {
        dismissActiveToasts();
        Logger.d(TAG, "Making Warning Toast");

        mSuperActivityToast = new SuperActivityToast(activity, Style.TYPE_STANDARD);

        mSuperActivityToast.setAnimations(Style.ANIMATIONS_FADE);
        mSuperActivityToast.setDuration(Style.DURATION_SHORT);
        mSuperActivityToast.setColor(Style.orange().color);
        mSuperActivityToast.setText(message);
        mSuperActivityToast.setTextSize(Style.TEXTSIZE_MEDIUM);

        mSuperActivityToast.show();
        return mSuperActivityToast;
    }


    /**
     * Create a Toast according to an enumerator
     *
     * @param mode enumerator
     */
    public SuperActivityToast makeToast(final Activity activity, final ErgoAlarmManager.ALARM_MODE mode) {
        if (mSuperActivityToast != null && mSuperActivityToast.isShowing()) {
            SuperActivityToast.cancelAllSuperToasts();
            ;
            mSuperActivityToast.dismiss();
        }

        switch (mode) {
            case NORMAL:
                final int interval = new AppUtils().getAppSharedPreferences(activity).getInt(
                        activity.getString(R.string.pref_alarm_interval_key), 1);
                mSuperActivityToast = new ToastUtils().makeInfoToast(activity, "Alarm set for " +
                                PrimitiveConversions.getTimeStringFromSeconds(interval * 60) + " from now",
                        Style.purple().color);
                break;
            case REPEAT:
                // get the stored value from shared preferences
                final int alarmDuration = new AppUtils().getAppSharedPreferences(activity).getInt(
                        activity.getString(R.string.pref_alarm_interval_key), 1);
                // make a card toast
                mSuperActivityToast = new ToastUtils().makeInfoToast(activity, "Repeating for " +
                                PrimitiveConversions.getTimeStringFromSeconds(alarmDuration * 60) + " from now",
                        Style.green().color);
                break;
            case SNOOZE:
                // get the stored value from shared preferences
                final int snoozeDuration = new AppUtils().getAppSharedPreferences(activity).getInt(
                        activity.getString(R.string.pref_snooze_interval_key), 1);
                // make a card toast
                mSuperActivityToast = new ToastUtils().makeInfoToast(activity, "Snoozing for " +
                                PrimitiveConversions.getTimeStringFromSeconds(snoozeDuration * 60) + " from now",
                        Style.orange().color);
                break;
            case AUTO:
                break;
            default:
                break;
        }
        return mSuperActivityToast;
    }

}
