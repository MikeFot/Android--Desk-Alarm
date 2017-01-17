package com.michaelfotiadis.deskalarm.utils;

import android.app.Activity;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.michaelfotiadis.deskalarm.ui.base.core.AlarmManagerImpl;
import com.michaelfotiadis.deskalarm.ui.base.core.preference.PreferenceHandler;
import com.michaelfotiadis.deskalarm.ui.base.core.preference.PreferenceHandlerImpl;
import com.michaelfotiadis.deskalarm.utils.log.AppLog;

public class ToastHelper {

    private final Activity mActivity;
    private final PreferenceHandler mPreferenceHandler;
    private SuperActivityToast mSuperActivityToast;

    public ToastHelper(final Activity activity) {
        this.mActivity = activity;
        this.mPreferenceHandler = new PreferenceHandlerImpl(activity);
    }

    /**
     * Method for dismissing all active toasts
     */
    public void dismissActiveToasts() {
        AppLog.d("Dismissing Toasts");
        if (mSuperActivityToast != null) {
            SuperActivityToast.cancelAllSuperToasts();
        }
    }

    public void makeInfoToast(final String message) {
        dismissActiveToasts();
        AppLog.d("Making Info Toast");

        mSuperActivityToast = new SuperActivityToast(mActivity, Style.TYPE_STANDARD);

        mSuperActivityToast.setAnimations(Style.ANIMATIONS_FADE);
        mSuperActivityToast.setDuration(Style.DURATION_SHORT);
        mSuperActivityToast.setColor(Style.green().color);
        mSuperActivityToast.setText(message);
        mSuperActivityToast.setTextSize(Style.TEXTSIZE_MEDIUM);
        mSuperActivityToast.setTouchToDismiss(true);
        mSuperActivityToast.show();
    }

    public SuperActivityToast makeInfoToast(final String message, final int color) {
        dismissActiveToasts();
        AppLog.d("Making Info Toast");

        mSuperActivityToast = new SuperActivityToast(mActivity, Style.TYPE_STANDARD);

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
        AppLog.d("Making Progress Toast");

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
     * @param message
     * @return
     */
    public SuperActivityToast makeWarningToast(final String message) {
        dismissActiveToasts();
        AppLog.d("Making Warning Toast");

        mSuperActivityToast = new SuperActivityToast(mActivity, Style.TYPE_STANDARD);

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
    public void makeToast(final AlarmManagerImpl.ALARM_MODE mode) {
        if (mSuperActivityToast != null && mSuperActivityToast.isShowing()) {
            SuperActivityToast.cancelAllSuperToasts();

            mSuperActivityToast.dismiss();
        }

        switch (mode) {
            case NORMAL:
                final int interval = mPreferenceHandler.getInt(PreferenceHandlerImpl.PreferenceKey.ALARM_INTERVAL);
                makeInfoToast(String.format(
                        "Alarm set for %s from now",
                        PrimitiveConversions.getTimeStringFromSeconds(interval * 60)),
                        Style.purple().color);
                break;
            case REPEAT:
                // get the stored value from shared preferences
                final int alarmDuration = mPreferenceHandler.getInt(PreferenceHandlerImpl.PreferenceKey.ALARM_INTERVAL);
                // make a card toast
                makeInfoToast(String.format(
                        "Repeating for %s from now",
                        PrimitiveConversions.getTimeStringFromSeconds(alarmDuration * 60)),
                        Style.green().color);
                break;
            case SNOOZE:
                // get the stored value from shared preferences
                final int snoozeDuration = mPreferenceHandler.getInt(PreferenceHandlerImpl.PreferenceKey.SNOOZE_INTERVAL);
                // make a card toast
                mSuperActivityToast = makeInfoToast(
                        String.format(
                                "Snoozing for %s from now",
                                PrimitiveConversions.getTimeStringFromSeconds(snoozeDuration * 60)),
                        Style.orange().color);
                break;
            case AUTO:
                break;
            default:
                break;
        }
    }

}
